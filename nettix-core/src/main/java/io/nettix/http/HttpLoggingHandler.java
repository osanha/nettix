package io.nettix.http;

import static io.nettix.util.Character.LF;
import static io.nettix.util.Character.SPACE;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nettix.channel.handler.MessageEventHandler;
import io.nettix.util.StringUtil;

/**
 * A channel handler that logs HTTP messages.
 *
 * @author sanha
 */
public class HttpLoggingHandler
        extends MessageEventHandler
{
  /**
   * Logger instance.
   */
  private final Logger _logger;

  /**
   * Static source hostname.
   */
  private String _staticFrom;

  /**
   * Static destination hostname.
   */
  private String _staticTo;

  /**
   * Source hostname.
   */
  private String _from;

  /**
   * Destination hostname.
   */
  private String _to;

  /**
   * Whether to log the object attached to the channel.
   */
  private boolean _attachmentLogging;

  /**
   * Constructor.
   */
  public HttpLoggingHandler()
  {
    _logger = LoggerFactory.getLogger(HttpLoggingHandler.class);
  }

  /**
   * Constructor.
   *
   * @param suffix
   *          Logger name suffix.
   */
  public HttpLoggingHandler(String suffix)
  {
    _logger = LoggerFactory.getLogger(HttpLoggingHandler.class.getName() + '.'
            + suffix);
  }

  /**
   * Sets the source hostname.
   *
   * @param from
   *          Hostname.
   * @return This object for method chaining.
   */
  public HttpLoggingHandler setFrom(String from)
  {
    _staticFrom = from;
    return this;
  }

  /**
   * Sets the destination hostname.
   *
   * @param to
   *          Hostname.
   * @return This object for method chaining.
   */
  public HttpLoggingHandler setTo(String to)
  {
    _staticTo = to;
    return this;
  }

  /**
   * Sets whether to log the object attached to the channel.
   *
   * @param enabled
   *          If true, logging is enabled.
   * @return This object for method chaining.
   */
  public HttpLoggingHandler setAttachmentLogging(boolean enabled)
  {
    _attachmentLogging = enabled;
    return this;
  }

  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
          throws Exception
  {
    log(e, " RECEIVED");
    ctx.sendUpstream(e);
  }

  @Override
  public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
          throws Exception
  {
    log(e, " SEND");
    ctx.sendDownstream(e);
  }

  /**
   * Performs logging.
   *
   * @param e
   *          Message event.
   * @param prefix
   *          Prefix to add to the log.
   */
  private void log(MessageEvent e, String prefix)
  {
    if (!_logger.isInfoEnabled())
      return;

    Object o = e.getMessage();

    if (!(o instanceof HttpMessage))
      return;

    StringBuilder buf = new StringBuilder();
    Channel ch = e.getChannel();

    if (_attachmentLogging)
    {
      Object attach = ch.getAttachment();

      if (attach != null)
        buf.append(attach.toString()).append(' ');
    }

    buf.append(ch.toString());
    buf.append(prefix);

    if (o instanceof HttpRequest)
      handleHttpRequest((HttpRequest) o, buf);
    else
      handleHttpResponse((HttpResponse) o, buf);

    _logger.info(buf.toString());
  }

  /**
   * Logs an HTTP request message.
   *
   * @param req
   *          Request message.
   * @param buf
   *          Logging buffer.
   */
  private void handleHttpRequest(HttpRequest req, StringBuilder buf)
  {
    _from = (_staticFrom != null) ? _staticFrom : HttpHeadersExt.getFrom(req);
    _to = (_staticTo != null) ? _staticTo : HttpHeaders.getHost(req);

    buf.append("\n=============================================\n");
    buf.append(" [");
    buf.append(_from);
    buf.append(" ===> ");
    buf.append(_to);
    buf.append("] ");
    buf.append(" HTTP Request information\n");
    buf.append("=============================================\n");
    buf.append(req.getMethod());
    buf.append(SPACE);
    buf.append(req.getPath());
    buf.append(SPACE);
    buf.append(req.getProtocolVersion());
    buf.append(LF);

    if (req.toStringParameters().length() > 0)
    {
      buf.append(req.toStringParameters());
      buf.append(LF);
    }

    handleHttpMessage(req, buf);
  }

  /**
   * Logs the common part of HTTP request/response messages.
   *
   * @param msg
   *          HTTP message.
   * @param buf
   *          Logging buffer.
   */
  private void handleHttpMessage(HttpMessage msg, StringBuilder buf)
  {
    if (msg.toStringHeaders() != null)
      buf.append(msg.toStringHeaders());

    if (msg.getContent().readable() && _logger.isDebugEnabled())
      buf.append(StringUtil.toHexDump(msg.getContent()));
  }

  /**
   * Logs an HTTP response message. Always logs the body when the status code indicates failure.
   *
   * @param res
   *          Response message.
   * @param buf
   *          Logging buffer.
   */
  private void handleHttpResponse(HttpResponse res, StringBuilder buf)
  {
    HttpResponseStatus status = res.getStatus();

    buf.append("\n=============================================\n");
    buf.append(" [");
    buf.append(_from);
    buf.append(" <--- ");
    buf.append(_to);
    buf.append("] ");
    buf.append(" HTTP Response information\n");
    buf.append("=============================================\n");
    buf.append(res.getProtocolVersion());
    buf.append(SPACE);
    buf.append(status.toString());
    buf.append(LF);

    handleHttpMessage(res, buf);
  }

}