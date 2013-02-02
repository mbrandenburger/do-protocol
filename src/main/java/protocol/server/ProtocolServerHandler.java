package protocol.server;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.LoggerFactory;
import protocol.common.ProtocolMessage;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/1/13
 * Time: 4:46 PM
 */
public class ProtocolServerHandler extends IdleStateAwareChannelHandler {

    private static final org.slf4j.Logger log =
            LoggerFactory.getLogger(ProtocolServerHandler.class);

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

        if (e.getMessage() instanceof ProtocolMessage) {
            protocolMessageReceived(ctx, (ProtocolMessage) e.getMessage());
        }
    }

    public void protocolMessageReceived(ChannelHandlerContext ctx, ProtocolMessage message) {

        log.debug("Received message " + message.toString() + " from " + ctx.getChannel().getRemoteAddress());

        sendProtocolMessage(ctx, message);

    }

    public void sendProtocolMessage(ChannelHandlerContext ctx, ProtocolMessage message) {

        ChannelFuture channelFuture = ctx.getChannel().write(message);

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                Channel channel = future.getChannel();
                channel.close();
            }
        });
    }

    public int generateParameterHash(String parameter) {
        HashFunction hashFunction = Hashing.md5();
        HashCode hash = hashFunction.newHasher().putString(parameter).hash();
        return hash.asInt();
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        log.warn("IdleAlarm! Closing channel " + e.getChannel().getRemoteAddress());
        e.getChannel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.warn("Unexpected exception", e.getCause());
        e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        log.debug("Connected to: " + ctx.getChannel().getRemoteAddress().toString());
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        log.debug("Channel closed: " + ctx.getChannel().getRemoteAddress().toString());
    }
}
