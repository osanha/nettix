package io.nettix.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.compression.JdkZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.jboss.netty.handler.codec.http.HttpContentEncoder;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.util.internal.DetectionUtil;

/**
 * A channel handler that compresses HTTP messages using a predefined encoding
 * regardless of the client's requested encoding.
 *
 * Author: Sanha
 */
public class HttpContentCompressor
        extends HttpContentEncoder
{
    /**
     * Indicates whether the JVM version is 7 or higher.
     */
    private static final boolean _isJVM7 = DetectionUtil.javaVersion() >= 7;

    /**
     * Compression level.
     */
    private final int _compressLevel;

    /**
     * Window bits size.
     */
    private final int _windowBits;

    /**
     * Memory level for compression.
     */
    private final int _memLevel;

    /**
     * Zlib wrapper type.
     */
    private final ZlibWrapper _wrapper;

    /**
     * The compression encoding to use.
     */
    private final HttpContentEncoding _encoding;

    /**
     * Constructor.
     *
     * @param encoding
     *          The compression encoding to use. Only gzip and deflate are supported.
     */
    public HttpContentCompressor(HttpContentEncoding encoding)
    {
        this(encoding, 6);
    }

    /**
     * Constructor.
     *
     * @param encoding
     *          The compression encoding to use. Only gzip and deflate are supported.
     * @param compressLevel
     *          Compression level, valid values are 1 through 9.
     */
    public HttpContentCompressor(HttpContentEncoding encoding, int compressLevel)
    {
        this(encoding, compressLevel, 15, 8);
    }

    /**
     * Constructor.
     *
     * @param encoding
     *          The compression encoding to use. Only gzip and deflate are supported.
     * @param compressLevel
     *          Compression level, valid values are 1 through 9.
     * @param windowBits
     *          Window bits size, valid values are 9 through 15.
     * @param memLevel
     *          Memory level, valid values are 1 through 9.
     */
    public HttpContentCompressor(HttpContentEncoding encoding, int compressLevel,
                                 int windowBits, int memLevel)
    {
        if ((compressLevel < 1) || (compressLevel > 9))
            throw new IllegalArgumentException("compressLevel must be between 1 and 9");

        if ((windowBits < 9) || (windowBits > 15))
            throw new IllegalArgumentException("windowBits must be between 9 and 15");

        if ((memLevel < 1) || (memLevel > 9))
            throw new IllegalArgumentException("memLevel must be between 1 and 9");

        if (encoding == HttpContentEncoding.deflate)
            _wrapper = ZlibWrapper.ZLIB;
        else
            _wrapper = ZlibWrapper.GZIP;

        _encoding = encoding;
        _compressLevel = compressLevel;
        _windowBits = windowBits;
        _memLevel = memLevel;
    }

    /**
     * Returns an encoder for the predefined encoding, ignoring the acceptEncoding header.
     */
    @Override
    protected EncoderEmbedder<ChannelBuffer> newContentEncoder(HttpMessage msg,
                                                               String acceptEncoding)
            throws Exception
    {
        if (_isJVM7)
            return new EncoderEmbedder<ChannelBuffer>(
                    new JdkZlibEncoder(_wrapper,
                            _compressLevel));
        else
            return new EncoderEmbedder<ChannelBuffer>(new ZlibEncoder(_wrapper,
                    _compressLevel,
                    _windowBits,
                    _memLevel));
    }

    /**
     * Returns the predefined content encoding, ignoring the acceptEncoding header.
     */
    @Override
    protected String getTargetContentEncoding(String acceptEncoding)
            throws Exception
    {
        return _encoding.name();
    }
}