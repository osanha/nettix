package io.nettix.http;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.PRECONDITION_FAILED;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

/**
 * HTTP utility class.
 * Provides common methods for handling HTTP responses and basic authentication.
 *
 * @author sanha
 */
public class HttpUtil
{
    /**
     * Checks whether the HTTP response status indicates success.
     * For simplicity, only checks that the status code is below 400.
     *
     * @param res the HTTP response
     * @return true if the response is successful
     */
    public static boolean isSuccess(HttpResponse res)
    {
        return res.getStatus().getCode() < 400;
    }

    /**
     * Parses a Basic Access Authentication string.
     *
     * @param authorization the authorization header value
     * @return the parsed Credential object
     * @throws HttpException if the format is invalid or not Basic authentication
     */
    public static Credential decodeBasicAccess(String authorization)
            throws HttpException
    {
        if (!authorization.startsWith("Basic "))
            throw new HttpException(PRECONDITION_FAILED,
                    "Only Basic Access Authentication is allowed: " + authorization);

        ChannelBuffer buf = ChannelBuffers.copiedBuffer(authorization, 6,
                authorization.length() - 6,
                CharsetUtil.ISO_8859_1);
        String[] auth = Base64.decode(buf).toString(CharsetUtil.UTF_8).split(":");

        if (auth.length != 2)
            throw new HttpException(BAD_REQUEST,
                    "Invalid Basic Access Authentication format: " + authorization);

        return new Credential(auth[0], auth[1]);
    }

    /**
     * Sends an HTTP response with the specified status.
     *
     * @param ch the channel
     * @param status the HTTP response status
     * @return the channel future
     */
    public static ChannelFuture writeResponse(Channel ch,
                                              HttpResponseStatus status)
    {
        return writeResponse(ch, new HttpResponse(status), null);
    }

    /**
     * Sends an HTTP response with the specified status and message body.
     *
     * @param ch the channel
     * @param status the HTTP response status
     * @param msg optional message to include in the body
     * @return the channel future
     */
    public static ChannelFuture writeResponse(Channel ch,
                                              HttpResponseStatus status,
                                              String msg)
    {
        return writeResponse(ch, new HttpResponse(status), msg);
    }

    /**
     * Sends an HTTP response.
     *
     * @param ch the channel
     * @param res the HTTP response object
     * @param msg optional message to include in the body
     * @return the channel future
     */
    public static ChannelFuture writeResponse(Channel ch, HttpResponse res,
                                              String msg)
    {
        if ((msg != null) && (msg.length() > 0))
            res.setContent(msg, HttpContentType.TEXT_PLAIN);

        return Channels.write(ch, res);
    }

    /**
     * Sends an HTTP response with the specified status using the channel context.
     *
     * @param ctx the channel handler context
     * @param status the HTTP response status
     * @return the channel future
     */
    public static ChannelFuture writeResponse(ChannelHandlerContext ctx,
                                              HttpResponseStatus status)
    {
        return writeResponse(ctx, new HttpResponse(status));
    }

    /**
     * Sends an HTTP response with the specified status and message body using the channel context.
     *
     * @param ctx the channel handler context
     * @param status the HTTP response status
     * @param msg optional message to include in the body
     * @return the channel future
     */
    public static ChannelFuture writeResponse(ChannelHandlerContext ctx,
                                              HttpResponseStatus status,
                                              String msg)
    {
        return writeResponse(ctx, new HttpResponse(status), msg);
    }

    /**
     * Sends an HTTP response using the channel context.
     *
     * @param ctx the channel handler context
     * @param res the HTTP response object
     * @return the channel future
     */
    public static ChannelFuture writeResponse(ChannelHandlerContext ctx,
                                              HttpResponse res)
    {
        return writeResponse(ctx, res, null);
    }

    /**
     * Sends an HTTP response with an optional message using the channel context.
     *
     * @param ctx the channel handler context
     * @param res the HTTP response object
     * @param msg optional message to include in the body
     * @return the channel future
     */
    public static ChannelFuture writeResponse(ChannelHandlerContext ctx,
                                              HttpResponse res, String msg)
    {
        if (msg != null)
            res.setContent(msg, HttpContentType.TEXT_PLAIN);

        ChannelFuture future = Channels.future(ctx.getChannel());
        Channels.write(ctx, future, res);
        return future;
    }

    /**
     * Sends a 204 No Content response.
     *
     * @param ch the channel to write to
     * @return the channel future
     */
    public static ChannelFuture writeNoContentResponse(Channel ch)
    {
        return Channels.write(ch, new HttpResponse(NO_CONTENT));
    }

    /**
     * Sends a 204 No Content response using the channel context.
     *
     * @param ctx the channel handler context
     * @return the channel future
     */
    public static ChannelFuture writeNoContentResponse(ChannelHandlerContext ctx)
    {
        return writeResponse(ctx, new HttpResponse(NO_CONTENT));
    }

}