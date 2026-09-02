

package io.nettix.smpp.server;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import io.nettix.smpp.SmppException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.ServerChannelManager;
import io.nettix.channel.handler.ChannelReadTimeoutHandler;
import io.nettix.smpp.SmppStatus;
import io.nettix.smpp.handler.SmppDecoder;
import io.nettix.smpp.handler.SmppEncoder;
import io.nettix.smpp.handler.SmppEnquireLinkHandler;
import io.nettix.smpp.handler.SmppLogger;
import io.nettix.smpp.pdu.BindTransceiver;
import io.nettix.smpp.pdu.BindTransceiverResp;
import io.nettix.smpp.pdu.DeliverSm;
import io.nettix.smpp.pdu.EnquireLink;
import io.nettix.smpp.pdu.EnquireLinkResp;
import io.nettix.smpp.pdu.GenericNack;
import io.nettix.smpp.pdu.ResponsePdu;
import io.nettix.smpp.pdu.SmppPdu;
import io.nettix.smpp.pdu.SubmitSm;
import io.nettix.smpp.pdu.SubmitSmResp;
import io.nettix.smpp.pdu.Unbind;
import io.nettix.smpp.pdu.UnbindResp;
import io.nettix.util.RoundRobinInteger;
import io.nettix.util.Singleton;
import io.nettix.util.StringUtil;
import io.nettix.util.TimeoutableMap;
import io.nettix.util.TimeoutableMap.TimeoutHandler;

/**
 * SMPP server
 *
 * @author sanha
 */
public class SmppServer
    extends ServerChannelManager
{
  /**
   * Logger
   */
  private static final Logger _logger = LoggerFactory.getLogger(SmppServer.class);

  /**
   * SMPP frame encoder
   */
  private static final ChannelHandler _frameEncoder = new LengthFieldPrepender(
                                                                               4,
                                                                               true);

  /**
   * SMPP message decoder
   */
  private static final ChannelHandler _smppDecoder = new SmppDecoder();

  /**
   * SMPP message encoder
   */
  private static final ChannelHandler _smppEncoder = new SmppEncoder();

  /**
   * Channel timeout handler
   */
  private static final ChannelHandler _timeoutHandler = new ChannelReadTimeoutHandler(
                                                                                      Singleton.Timer,
                                                                                      10);

  /**
   * SMPP logger
   */
  private final ChannelHandler _smppLogger;

  /**
   * Connection check message transmission interval
   */
  private int _enquireLinkDelay = 60;

  /**
   * SMPP server handler
   */
  private final ChannelHandler _smppHandler = new SmppServerHandler();

  /**
   * Map for SME authentication
   */
  private Map<String, String> _credential;

  /**
   * Connection check handler
   */
  private ChannelHandler _enquirer;

  /**
   * Message listener
   */
  private SmppServerListener _listener;

  /**
   * MO timeout handler
   */
  private TimeoutableMap<Integer, ChannelFuture> _futureMap;

  /**
   * Sequence generator
   */
  private final RoundRobinInteger _sequencer = new RoundRobinInteger(
                                                                     1,
                                                                     SmppPdu.MAX_SEQUENCE);

  /**
   * Connected SME channels
   */
  private final ConcurrentHashMap<Channel, String> _smes = new ConcurrentHashMap<Channel, String>();

  /**
   * Random generator. Used for load balancing connected SMEs.
   */
  private final Random _random = new Random();

  /**
   * Default MO timeout duration (seconds)
   */
  private int _resTimeout = 10;

  /**
   * SMPP server handler
   *
   * @author sanha
   */
  class SmppServerHandler
      extends SimpleChannelUpstreamHandler
  {
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
                                    ChannelStateEvent e) throws Exception
    {
      String sme = _smes.remove(e.getChannel());

      if (sme != null)
        _logger.info("{} is unbounded", sme);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
        throws Exception
    {
      Channel ch = e.getChannel();
      SmppPdu pdu = (SmppPdu) e.getMessage();
      int seq = pdu.getSequence();

      if (pdu instanceof BindTransceiver)
        {
          BindTransceiver bind = (BindTransceiver) pdu;
          BindTransceiverResp rePdu = new BindTransceiverResp();
          rePdu.setSystemId(name());
          String id = bind.getSystemId();

          if (_credential != null)
            {
              String pw = bind.getPassword();
              String credPw = _credential.get(id);

              if (credPw == null)
                {
                  rePdu.setStatus(SmppStatus.ESME_RINVSYSID);
                  sendResponse(ch, rePdu, seq, true);
                  return;
                }

              if (!pw.equals(credPw))
                {
                  rePdu.setStatus(SmppStatus.ESME_RINVPASWD);
                  sendResponse(ch, rePdu, seq, true);
                  return;
                }
            }

          _smes.put(ch, id);
          sendResponse(ch, rePdu, seq, false);
          ch.getPipeline().replace(_timeoutHandler, "ENQUIRE_LINKER", _enquirer);
          _logger.info("{} is bounded successfully", id);
        }
      else if (pdu instanceof SubmitSm)
        {
          SubmitSm sm = (SubmitSm) pdu;
          String sender = sm.getSourceAddr();
          String receiver = sm.getDestinationAddr();

          SmppPdu rePdu = new SubmitSmResp();
          String msgId = StringUtil.randomUUID(16);
          ((SubmitSmResp) rePdu).setMessageId(msgId);
          sendResponse(ch, rePdu, seq, false);

          if (_listener != null)
            _listener.messageReceived(msgId, sender, receiver,
                                      sm.getShortMessage());
        }
      else if (pdu instanceof ResponsePdu)
        {
          ChannelFuture future = _futureMap.remove(seq);

          if (future != null)
            {
              SmppStatus status = pdu.getStatus();

              if (status == SmppStatus.ESME_ROK)
                future.setSuccess();
              else
                future.setFailure(new SmppException(status.toString()));
            }
        }
      else if (pdu instanceof EnquireLink)
        {
          SmppPdu rePdu = new EnquireLinkResp();
          sendResponse(ch, rePdu, seq, false);
        }
      else if (pdu instanceof Unbind)
        {
          SmppPdu rePdu = new UnbindResp();
          sendResponse(ch, rePdu, seq, true);
        }
      else
        {
          SmppPdu rePdu = new GenericNack();
          rePdu.setStatus(SmppStatus.ESME_RINVCMDID);
          sendResponse(ch, rePdu, seq, false);
        }
    }

    /**
     * Sends a response.
     *
     * @param ch
     *          channel
     * @param pdu
     *          SMPP message
     * @param seq
     *          sequence
     * @param isClose
     *          whether to close the channel after sending
     */
    private void sendResponse(Channel ch, SmppPdu pdu, int seq, boolean isClose)
    {
      pdu.setSequence(seq);
      ChannelFuture future = ch.write(pdu);

      if (isClose)
        future.addListener(ChannelFutureListener.CLOSE);
    }

  }

  /**
   * Constructor.
   *
   * @param name
   *          name
   * @param port
   *          port
   */
  public SmppServer(String name, int port)
  {
    super(name, port);
    _smppLogger = new SmppLogger(name);
  }

  /**
   * Sets the SME authentication map.
   *
   * @param credential
   *          authentication map
   */
  public void setCredential(Map<String, String> credential)
  {
    _credential = credential;
  }

  /**
   * Closes the connection to a specific SME.
   *
   * @param systemId
   *          id used during bind
   */
  public void close(String systemId)
  {
    for (Entry<Channel, String> sme : _smes.entrySet())
      {
        if (sme.getValue().equals(systemId))
          {
            sme.getKey().close();
            break;
          }
      }
  }

  /**
   * Sets the listener to handle MT messages.
   *
   * @param listener
   *          listener
   */
  public void setListener(SmppServerListener listener)
  {
    _listener = listener;
  }

  /**
   * Sets the timeout duration for sending MO messages and waiting for a response.
   *
   * @param timeout
   *          seconds
   */
  public void setResTimeout(int timeout)
  {
    _resTimeout = timeout;
  }

  /**
   * Sets the interval for channel connection check messages.
   *
   * @param delay
   *          transmission interval (seconds)
   */
  public void setEnquireLinkDelay(int delay)
  {
    _enquireLinkDelay = delay;
  }

  @Override
  public ChannelPipeline getPipeline() throws Exception
  {
    ChannelPipeline pipeline = super.getPipeline();

    pipeline.addLast("FRAME_DECODER",
                     new LengthFieldBasedFrameDecoder(SmppPdu.MAX_PDU_LEN, 0,
                                                      4, -4, 4));
    pipeline.addLast("SMPP_DECODER", _smppDecoder);
    pipeline.addLast("FRAME_ENCODER", _frameEncoder);
    pipeline.addLast("SMPP_ENCODER", _smppEncoder);
    pipeline.addLast("SMPP_LOGGER", _smppLogger);
    pipeline.addLast("TIMEOUT_HANDLER", _timeoutHandler);
    pipeline.addLast("SMPP_HANDLER", _smppHandler);

    return pipeline;
  }

  /**
   * Handles the transmission of MO SMS.
   *
   * @param sender
   *          sender number
   * @param receiver
   *          receiver number
   * @param msg
   *          SMS message
   * @return processing result
   */
  public ChannelFuture writeRequested(String sender, String receiver,
                                      ChannelBuffer msg)
  {
    return writeRequested(sender, receiver, msg, false);
  }

  /**
   * Handles MO transmission.
   *
   * @param sender
   *          sender number
   * @param receiver
   *          receiver number
   * @param msg
   *          message
   * @param isDeliveryReceipt
   *          whether this is a delivery receipt notification
   * @return processing result
   */
  public ChannelFuture writeRequested(String sender, String receiver,
                                      ChannelBuffer msg,
                                      boolean isDeliveryReceipt)
  {
    if (_smes.isEmpty())
      return Channels.failedFuture(serverChannel(),
                                   new SmppException("Not exist avaiable SME"));

    Object[] tmp = _smes.keySet().toArray();
    Channel ch = (Channel) tmp[_random.nextInt(tmp.length)];
    final ChannelFuture future = Channels.future(ch);
    final int sequence = _sequencer.next();

    DeliverSm sm = new DeliverSm();
    sm.setSequence(sequence);
    sm.setDataCoding(4);
    sm.setSourceAddr(sender);
    sm.setDestinationAddr(receiver);
    sm.setSmLength(msg.readableBytes());
    sm.setShortMessage(msg);

    if (isDeliveryReceipt)
      sm.setEsmClass(0x04);

    ch.write(sm).addListener(new ChannelFutureListener()
    {
      @Override
      public void operationComplete(ChannelFuture f) throws Exception
      {
        if (f.isSuccess())
          _futureMap.put(sequence, future);
        else
          future.setFailure(f.getCause());
      }
    });

    return future;
  }

  @Override
  public void setUp() throws Exception
  {
    _futureMap = new TimeoutableMap<Integer, ChannelFuture>(name()
                                                            + " SMPP-PDU",
                                                            _resTimeout);
    _futureMap.setTimeoutHandler(new TimeoutHandler<Integer, ChannelFuture>()
    {
      @Override
      public void handleTimeout(Integer seq, ChannelFuture future)
      {
        future.setFailure(new ReadTimeoutException());
      }
    });

    _enquirer = new SmppEnquireLinkHandler(name(), _enquireLinkDelay,
                                           _enquireLinkDelay + _resTimeout,
                                           _sequencer);

    super.setUp();
  }

}
