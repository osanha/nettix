

package io.nettix.smpp.client;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;

import io.nettix.smpp.SmppException;
import io.nettix.smpp.SmppStatus;
import io.nettix.smpp.conf.SmppClientConf;
import io.nettix.smpp.conf.SmppServerConf;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.CallableChannelFuture;
import io.nettix.channel.PersistentClientChannelManager;
import io.nettix.smpp.conf.SmppBindConf;
import io.nettix.smpp.handler.SmppDecoder;
import io.nettix.smpp.handler.SmppEncoder;
import io.nettix.smpp.handler.SmppEnquireLinkHandler;
import io.nettix.smpp.handler.SmppLogger;
import io.nettix.smpp.mt.DefaultDeliveryReceipt;
import io.nettix.smpp.mt.DeliveryReceipt;
import io.nettix.smpp.mt.Result;
import io.nettix.smpp.pdu.BindPdu;
import io.nettix.smpp.pdu.BindReceiver;
import io.nettix.smpp.pdu.BindTransceiver;
import io.nettix.smpp.pdu.BindTransmitter;
import io.nettix.smpp.pdu.DeliverSm;
import io.nettix.smpp.pdu.DeliverSmResp;
import io.nettix.smpp.pdu.EnquireLink;
import io.nettix.smpp.pdu.EnquireLinkResp;
import io.nettix.smpp.pdu.ResponsePdu;
import io.nettix.smpp.pdu.SmppPdu;
import io.nettix.smpp.pdu.SubmitSm;
import io.nettix.smpp.pdu.SubmitSmResp;
import io.nettix.smpp.pdu.Unbind;
import io.nettix.util.RoundRobinInteger;
import io.nettix.util.TimeoutableMap;
import io.nettix.util.TimeoutableMap.TimeoutHandler;

/**
 * SMPP-based SMS Client
 *
 * @author sanha
 */
public class SmppClient
    extends PersistentClientChannelManager
{
  /**
   * SMPP frame encoder
   */
  private static final ChannelHandler _frameEncoder = new LengthFieldPrepender(
                                                                               4,
                                                                               true);

  /**
   * SMSC ID
   */
  private final String _id;

  /**
   * SMS event handler
   */

  private SmppClientListener _listener;

  /**
   * SMPP message decoder
   */
  private static final ChannelHandler _smppDecoder = new SmppDecoder();

  /**
   * SMPP message encoder
   */
  private static final ChannelHandler _smppEncoder = new SmppEncoder();

  /**
   * Logger
   */
  private final Logger _logger;

  /**
   * SMPP logger
   */
  private final ChannelHandler _smppLogger;

  /**
   * SMPP handler
   */
  private final ChannelHandler _smppHandler = new SmppClientHandler();

  /**
   * Keep-alive handler
   */
  private final ChannelHandler _enquireLinker;

  /**
   * Delivery receipt handler
   */
  private final DeliveryReceipt _deliveryReceipt;

  /**
   * Configuration
   */
  private final SmppClientConf _conf;


  /**
   * Connection address for MO-SMS reception
   */
  private final SocketAddress _moAddr;

  /**
   * Connection address for MT-SMS transmission
   */
  private final SocketAddress _mtAddr;

  /**
   * Whether using bind_transceiver mode
   */
  private final boolean _isTransceiver;

  /**
   * Sequence generator
   */
  private final RoundRobinInteger _sequencer = new RoundRobinInteger(
                                                                     1,
                                                                     SmppPdu.MAX_SEQUENCE);

  /**
   * Transmission result map
   */
  private final TimeoutableMap<Integer, ChannelFuture> _futureMap;

  /**
   * MT channel
   */
  private Channel _mtCh;

  /**
   * MO channel
   */
  private Channel _moCh;

  /**
   * Validity period string
   */
  private String _validityPeriod;


  /**
   * SMPP client handler
   *
   * @author sanha
   */
  @Sharable
  private class SmppClientHandler
      extends SimpleChannelUpstreamHandler
  {
    /**
     * Process bind
     *
     * @param ch
     *          channel
     * @param pdu
     *          Bind message
     * @return result
     */
    private ChannelFuture bind(Channel ch, BindPdu pdu)
    {
      int sequence = _sequencer.next();
      SmppBindConf bindConf = _conf.bindConf();

      pdu.setSystemId(bindConf.getSystemId());
      pdu.setPassword(bindConf.getPassword());
      pdu.setSystemType(bindConf.getSystemType());
      pdu.setInterfaceVersion(bindConf.getInterfaceVersion());
      pdu.setAddrTon(bindConf.getAddrTon());
      pdu.setAddrNpi(bindConf.getAddrNpi());
      pdu.setAddressRange(bindConf.getAddressRange());
      pdu.setSequence(sequence);
      _logger.info("Sending {}", pdu.getId().name());

      return reqWrite(ch, sequence, pdu);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
        throws Exception
    {
      if ((e.getCause() instanceof IOException)
          && ctx.getChannel().isConnected())
        ctx.getChannel().close();
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx,
                                 ChannelStateEvent e) throws Exception
    {
      final Channel ch = ctx.getChannel();
      BindPdu pdu;

      if (_isTransceiver)
        {
          _mtCh = ch;
          pdu = new BindTransceiver();
        }
      else
        {
          SocketAddress remoteAddr = ch.getRemoteAddress();

          if (remoteAddr.equals(_mtAddr))
            {
              _mtCh = ch;
              pdu = new BindTransmitter();
            }
          else
            {
              _moCh = ch;
              pdu = new BindReceiver();
            }
        }

      bind(ch, pdu).addListener(new ChannelFutureListener()
      {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception
        {
          if (future.isSuccess())
            {
              ch.getPipeline().addBefore(ctx.getName(), "ENQUIRE_LINKER",
                                         _enquireLinker);
            }
          else
            {
              _logger.error("Bind failed", future.getCause());
              ch.close();
            }
        }
      });
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
                                    ChannelStateEvent e) throws Exception
    {
      if (!isBounded(ctx.getChannel()) || (state() == State.TERMINATED))
        return;

      if (_isTransceiver)
        {
          _mtCh = null;
          _logger.error("SMPP MO/MT channel was disconnected");
        }
      else if (ctx.getChannel().equals(_mtCh))
        {
          _mtCh = null;
          _logger.error("SMPP MT channel was disconnected");
        }
      else
        {
          _moCh = null;
          _logger.error("SMPP MO channel was disconnected");
        }
    }

    /**
     * Handles delivery receipt.
     *
     * @param sms
     *          delivery receipt message
     */
    private void handleDeliveryReceipt(DeliverSm sms)
    {
      Result result = _deliveryReceipt.parse(sms.getShortMessage());
      String msgId = result.msgId();
      String state = result.state();
      _logger.info("delivery receipted: msgId={}, state={}", msgId, state);

      if (state.equals("DELIVRD"))
        _listener.mtSuccess(_id, msgId);
      else
        _listener.mtFailure(_id, msgId, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
        throws Exception
    {
      Channel ch = e.getChannel();
      SmppPdu pdu = (SmppPdu) e.getMessage();
      int sequence = pdu.getSequence();

      if (pdu instanceof DeliverSm)
        {
          DeliverSmResp rePdu = new DeliverSmResp();
          rePdu.setSequence(sequence);
          ch.write(rePdu);

          DeliverSm sms = (DeliverSm) pdu;
          int esmClass = sms.getEsmClass();

          if (esmClass == 0x04)
            handleDeliveryReceipt(sms);
          else if (esmClass == 0x08)
            _logger.info("Received delivery acknowledgement");
          else if (esmClass == 0x00)
            handleMO(sms);
          else
            _logger.error("Received unknown deliver_sm. (esm_class={})",
                          esmClass);
        }
      else if (pdu instanceof ResponsePdu)
        {
          SmppStatus status = pdu.getStatus();
          ChannelFuture f = _futureMap.remove(sequence);

          if (f != null)
            {
              if (status == SmppStatus.ESME_ROK)
                {
                  if (pdu instanceof SubmitSmResp)
                    {
                      String msgId = ((SubmitSmResp) pdu).getMessageId();
                      ((CallableChannelFuture<String>) f).setSuccess(msgId);
                    }
                  else
                    {
                      f.setSuccess();
                    }
                }
              else
                {
                  f.setFailure(new SmppException(status.toString()));
                }
            }
        }
      else if (pdu instanceof EnquireLink)
        {
          EnquireLinkResp rePdu = new EnquireLinkResp();
          rePdu.setSequence(sequence);
          ch.write(rePdu);
        }
    }
  }

  /**
   * SMPP client
   *
   * @param conf configuration
   * @param listener SMS event handler
   */
  public SmppClient(String id, SmppClientConf conf, SmppClientListener listener)
  {
    this(id, conf, listener, new DefaultDeliveryReceipt());
  }

  /**
   * SMPP client. Custom handler can be set for non-standard delivery_receipt.
   *
   * @param id SMSC identifier name
   * @param conf configuration
   * @param listener SMS event handler
   * @param receipt MT delivery result handler
   */
  public SmppClient(String id, SmppClientConf conf, SmppClientListener listener, DeliveryReceipt receipt)
  {
    super(id);
    _id = id;
    _logger = LoggerFactory.getLogger(SmppClient.class.getName() + '.' + id);
    _smppLogger = new SmppLogger(id);
    _conf = conf;
    _listener = listener;
    _deliveryReceipt = receipt;

    SmppServerConf smsc = _conf.serverConf();
    this.setReconnDelay(smsc.getReconnDelay());
    this.setConnInterval(smsc.getConnInterval());
    this.setConnTimeout(smsc.getConnTimeout());

    _moAddr = new InetSocketAddress(smsc.getAddress(), smsc.getMoPort());
    _mtAddr = new InetSocketAddress(smsc.getAddress(), smsc.getMtPort());
    _isTransceiver = _mtAddr.equals(_moAddr);

    _futureMap = new TimeoutableMap<Integer, ChannelFuture>(
                                                            id,
                                                            _conf.serverConf().getRespTimeout());
    _futureMap.setTimeoutHandler(new TimeoutHandler<Integer, ChannelFuture>()
    {
      @Override
      public void handleTimeout(Integer sequence, ChannelFuture future)
      {
        future.setFailure(new ReadTimeoutException());
      }
    });

    int delay = _conf.serverConf().getEnquireLink();
    int timeout = delay + _conf.serverConf().getRespTimeout();
    _enquireLinker = new SmppEnquireLinkHandler(name(), delay, timeout,
                                                _sequencer);
  }

  /**
   * Sets the SMS event handler.
   *
   * @param listener SMS event handler
   */
  public void setListener(SmppClientListener listener)
  {
    _listener = listener;
  }

  /**
   * Handles MO SMS.
   *
   * @param sms
   *          MO SMS
   * @throws Exception
   */
  private void handleMO(DeliverSm sms) throws Exception
  {
    ChannelBuffer msg = sms.getShortMessage();
    String sender = sms.getSourceAddr();
    _listener.moReceived(_id, sender, msg);
  }

  /**
   * Processes unbind.
   *
   * @param ch
   *          channel
   * @return result
   */
  private ChannelFuture unbind(Channel ch)
  {
    if ((ch != null) && ch.isConnected() && isBounded(ch))
      {
        int sequence = _sequencer.next();
        SmppPdu pdu = new Unbind();
        pdu.setSequence(sequence);
        return reqWrite(ch, sequence, pdu);
      }
    else
      {
        return null;
      }
  }

  @Override
  public void tearDown() throws Exception
  {
    // Send both asynchronously first, then wait sequentially.
    ChannelFuture mtFuture = unbind(_mtCh);

    if (!_isTransceiver)
      {
        ChannelFuture moFuture = unbind(_moCh);

        if (moFuture != null)
          moFuture.await(_conf.serverConf().getRespTimeout(), TimeUnit.SECONDS);
      }

    if (mtFuture != null)
      mtFuture.await(_conf.serverConf().getRespTimeout(), TimeUnit.SECONDS);

    super.tearDown();
  }

  /**
   * Sends a request message.
   *
   * @param ch
   *          channel
   * @param sequence
   *          sequence number
   * @param pdu
   *          message
   * @return result
   */
  private CallableChannelFuture<String> reqWrite(Channel ch,
                                                 final int sequence, SmppPdu pdu)
  {
    final CallableChannelFuture<String> ackFuture = new CallableChannelFuture<String>();
    // Put in map first since response may arrive before future listener is processed.
    _futureMap.put(sequence, ackFuture);
    ch.write(pdu).addListener(new ChannelFutureListener()
    {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception
      {
        if (!future.isSuccess())
          _futureMap.remove(sequence).setFailure(future.getCause());
      }
    });

    return ackFuture;
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    if (_isTransceiver)
      {
        connect(_mtAddr);
      }
    else
      {
        connect(_mtAddr);
        connect(_moAddr);
      }
  }

  /**
   * Returns whether the channel is bound.
   *
   * @param ch
   *          channel
   * @return true if bound
   */
  private boolean isBounded(Channel ch)
  {
    return (ch != null)
           && (ch.getPipeline().getContext(_enquireLinker) != null);
  }

  /**
   * Handles MT SMS.
   *
   * @param ch
   *          channel
   * @param receiver
   *          receiver number
   * @param msg
   *          message
   * @return result
   */
  private CallableChannelFuture<String> handleMT(Channel ch, String receiver,
                                                 ChannelBuffer msg)
  {
    int sequence = _sequencer.next();
    SubmitSm pdu = new SubmitSm(_conf.submitConf());

    pdu.setSequence(sequence);
    pdu.setDestinationAddr(receiver);
    pdu.setValidityPeriod(_conf.submitConf().getValidityPeriod());
    pdu.setSmLength(msg.readableBytes());
    pdu.setShortMessage(msg);

    return reqWrite(ch, sequence, pdu);
  }

  public CallableChannelFuture<String> sendMessage(String receiver,
                                                   ChannelBuffer msg)
      throws Exception
  {
    if ((_mtCh != null) && _mtCh.isConnected())
      {
        if (isBounded(_mtCh))
          return handleMT(_mtCh, receiver, msg);
        else
          return new CallableChannelFuture<String>(
                                                   _mtCh,
                                                   new BindException(
                                                                     "not yet bounded"));
      }
    else
      {
        return new CallableChannelFuture<String>(_mtCh,
                                                 new ClosedChannelException());
      }
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
    pipeline.addLast("SMPP_HANDLER", _smppHandler);

    return pipeline;
  }

  public boolean isActive()
  {
    return isBounded(_mtCh) && (_isTransceiver || isBounded(_moCh));
  }

}
