

package io.nettix.smpp.pdu;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;

import io.nettix.smpp.SMPP;
import io.nettix.smpp.SmppCommand;
import io.nettix.smpp.SmppOption;
import io.nettix.smpp.SmppStatus;
import io.nettix.util.StringUtil;

/**
 * SMPP PDU
 * 
 * @author sanha
 */
public abstract class SmppPdu
{
  /**
   * Maximum allowed PDU length
   */
  public static final int MAX_PDU_LEN = 1024;

  /**
   * Maximum sequence value
   */
  public static final int MAX_SEQUENCE = 0x7FFFFFFF;

  /**
   * Default buffer length for PDU encoding.
   */
  private static final int DEFAULT_PDU_LEN = 256;

  /**
   * NULL character value
   */
  public static final int NULL = 0x00;

  /**
   * SMPP PDU type ID
   */
  private SmppCommand id;

  /**
   * SMPP status code
   */
  private SmppStatus status;

  /**
   * Sequence value
   */
  private int sequence;

  /**
   * Optional parameter list
   */
  private List<OptionalParam> options;

  /**
   * Optional parameter
   *
   * @author sanha
   */
  public class OptionalParam
  {
    /**
     * Option tag
     */
    private SmppOption tag;

    /**
     * Option value
     */
    private ChannelBuffer value;

    /**
     * Returns the option tag
     *
     * @return option tag
     */
    public SmppOption getTag()
    {
      return tag;
    }

    /**
     * Returns the option value.
     *
     * @return option value
     */
    public ChannelBuffer getValue()
    {
      return value;
    }

    /**
     * Constructor.
     *
     * @param tag
     *          option tag
     * @param value
     *          option value
     */
    public OptionalParam(SmppOption tag, ChannelBuffer value)
    {
      this.tag = tag;
      this.value = value;
    }
  }

  /**
   * Returns the optional parameter list.
   *
   * @return optional parameter list
   */
  public List<OptionalParam> getOptions()
  {
    if (options == null)
      options = new ArrayList<OptionalParam>();

    return options;
  }

  /**
   * Returns the PDU type ID.
   *
   * @return ID
   */
  public SmppCommand getId()
  {
    return id;
  }

  /**
   * Sets the PDU type ID.
   *
   * @param id
   */
  public void setId(SmppCommand id)
  {
    this.id = id;
  }

  /**
   * Returns the SMPP status.
   *
   * @return status
   */
  public SmppStatus getStatus()
  {
    return status;
  }

  /**
   * Sets the SMPP status.
   *
   * @param status
   */
  public void setStatus(SmppStatus status)
  {
    this.status = status;
  }

  /**
   * Returns the sequence.
   *
   * @return sequence
   */
  public int getSequence()
  {
    return sequence;
  }

  /**
   * Sets the sequence.
   *
   * @param sequence
   */
  public void setSequence(int sequence)
  {
    this.sequence = sequence;
  }

  /**
   * Constructor.
   *
   * @param id
   *          SMPP PDU type ID
   */
  public SmppPdu(SmppCommand id)
  {
    this(id, SmppStatus.ESME_ROK);
  }

  /**
   * Constructor.
   *
   * @param id
   *          SMPP type ID
   * @param status
   *          status
   */
  public SmppPdu(SmppCommand id, SmppStatus status)
  {
    this.id = id;
    this.status = status;
  }

  /**
   * Encodes a string.
   *
   * @param cb
   *          target buffer
   * @param str
   *          string
   */
  protected void writeString(ChannelBuffer cb, String str)
  {
    if (str != null)
      cb.writeBytes(str.getBytes(CharsetUtil.UTF_8));

    cb.writeByte(NULL);
  }

  /**
   * Decodes a string.
   *
   * @param cb
   *          target buffer
   * @return string
   */
  protected String readString(ChannelBuffer cb)
  {
    int index = cb.readerIndex();
    int delta = 0;

    while (cb.readable())
      {
        if (cb.readByte() == NULL)
          {
            delta = -1;
            break;
          }
      }

    int length = cb.readerIndex() - index + delta;

    if (length > 0)
      return cb.toString(index, length, CharsetUtil.UTF_8);
    else
      return null;
  }

  /**
   * Decodes the PDU.
   *
   * @param cb
   *          target buffer
   */
  public void decode(ChannelBuffer cb)
  {
    decodeHeader(cb);
    decodeMandatory(cb);
    decodeOptional(cb);
  }

  /**
   * Decodes mandatory parameters
   *
   * @param cb
   *          target buffer
   */
  protected void decodeMandatory(ChannelBuffer cb)
  {
  }

  /**
   * Decodes optional parameters
   *
   * @param cb
   *          target buffer
   */
  protected void decodeOptional(ChannelBuffer cb)
  {
    while (cb.readable())
      {
        short tag = cb.readShort();
        SmppOption key = SMPP.getOption(tag);

        short length = cb.readShort();
        ChannelBuffer value = cb.readSlice(length);
        this.getOptions().add(new OptionalParam(key, value));
      }
  }

  /**
   * Decodes the header
   *
   * @param cb
   *          target buffer
   */
  private void decodeHeader(ChannelBuffer cb)
  {
    this.status = SMPP.getStatus(cb.readInt());
    this.sequence = cb.readInt();
  }

  /**
   * Encodes the PDU.
   *
   * @return encoded buffer
   */
  public ChannelBuffer encode()
  {
    ChannelBuffer cb = ChannelBuffers.dynamicBuffer(DEFAULT_PDU_LEN);
    encodeHeader(cb);
    encodeMandatory(cb);
    encodeOptional(cb);
    return cb;
  }

  @Override
  public String toString()
  {
    return toString(true);
  }

  /**
   * Outputs as a string.
   *
   * @param isDebug
   *          whether debug level. When true, short_message is also logged as HEX.
   * @return string
   */
  public String toString(boolean isDebug)
  {
    Formatter fmt = new Formatter(Locale.US);
    fmt.format("------------------------------------------------------\n");
    this.toStringHeader(fmt);
    fmt.format("------------------------------------------------------\n");
    this.toStringMandatory(fmt, isDebug);
    this.toStringOptional(fmt);
    String result = fmt.toString();
    fmt.close();
    return result;
  }

  /**
   * Outputs the header as a string.
   *
   * @param fmt
   *          target formatter
   */
  private void toStringHeader(Formatter fmt)
  {
    fmt.format("%15s : 0x%08X (%s)\n", "command_id", id.value(), id);
    fmt.format("%15s : 0x%08X (%s)\n", "command_status", status.value(), status);
    fmt.format("%15s : 0x%08X\n", "sequence_number", this.sequence);
  }

  /**
   * Outputs mandatory parameters as a string.
   *
   * @param fmt
   *          target formatter
   * @param isDebug
   *          whether debug level
   */
  protected void toStringMandatory(Formatter fmt, boolean isDebug)
  {
  }

  /**
   * Outputs optional parameters as a string.
   *
   * @param fmt
   *          target formatter
   */
  private void toStringOptional(Formatter fmt)
  {
    if ((this.options != null) && (options.size() > 0))
      {
        fmt.format("------------------------------------------------------\n");

        for (OptionalParam param : options)
          {
            fmt.format("Tag    : 0x%04X (%s)\n", param.getTag().value(),
                       param.getTag());
            fmt.format("Length : 0x%04X\n", param.getValue().readableBytes());
            fmt.format("Value  :\n%s\n", StringUtil.toHexDump(param.getValue()));
          }
      }
  }

  /**
   * Encodes the header.
   *
   * @param cb
   *          target buffer
   */
  private void encodeHeader(ChannelBuffer cb)
  {
    cb.writeInt(this.id.value());
    cb.writeInt(this.status.value());
    cb.writeInt(this.sequence);
  }

  /**
   * Encodes mandatory parameters.
   *
   * @param cb
   *          target buffer
   */
  protected void encodeMandatory(ChannelBuffer cb)
  {

  }

  /**
   * Encodes optional parameters.
   *
   * @param cb
   *          target buffer
   */
  private void encodeOptional(ChannelBuffer cb)
  {
    if (this.options != null)
      {
        for (OptionalParam param : options)
          {
            cb.writeShort(param.getTag().value());
            cb.writeShort(param.getValue().readableBytes());
            cb.writeBytes(param.getValue());
          }
      }
  }

}
