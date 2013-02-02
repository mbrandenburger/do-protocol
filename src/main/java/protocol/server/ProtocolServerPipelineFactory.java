package protocol.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.util.Timer;
import protocol.common.ProtocolHandler;

import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/1/13
 * Time: 5:51 PM
 */

public class ProtocolServerPipelineFactory implements ChannelPipelineFactory {

    private final int MAX_FRAME_LENGTH = 4096;
    private final ChannelBuffer[] FRAME_DELIMITER = Delimiters.lineDelimiter();
    private final Charset STRING_CHARSET = CharsetUtil.UTF_8;

    // Timeouts in seconds
    private final int READ_TIMEOUT = 0;
    private final int WRITE_TIMEOUT = 0;
    private final int ALL_TIMEOUT = 30;

    private Timer idleTimer;

    public ProtocolServerPipelineFactory(Timer idleTimer) {
        this.idleTimer = idleTimer;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {

        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("framer",
                new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH,
                        FRAME_DELIMITER));
        pipeline.addLast("str_decoder", new StringDecoder(STRING_CHARSET));
        pipeline.addLast("str_encoder", new  StringEncoder(STRING_CHARSET));
        pipeline.addLast("idleTimer",
                new IdleStateHandler(this.idleTimer, READ_TIMEOUT,
                        WRITE_TIMEOUT, ALL_TIMEOUT));
        pipeline.addLast("protocol_handler", new ProtocolHandler());
        pipeline.addLast("handler", new ProtocolServerHandler());

        return pipeline;
    }
}
