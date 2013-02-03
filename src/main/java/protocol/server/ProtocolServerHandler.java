package protocol.server;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.LoggerFactory;
import protocol.common.ProtocolMessage;
import protocol.common.ProtocolMessageFactory;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/1/13
 * Time: 4:46 PM
 */
public class ProtocolServerHandler extends IdleStateAwareChannelHandler {

    private static final org.slf4j.Logger log =
            LoggerFactory.getLogger(ProtocolServerHandler.class);

    private final HashFunction hashFunction = Hashing.md5();

    public void protocolMessageReceived(ChannelHandlerContext ctx,
                                        ProtocolMessage message) {

        // This is our business logic. It receives a ProtocolMessage m and
        // generate a hash string of m.parameter. This hash string will be send
        // as a response Message to our client

        log.debug("Received message " + message.getParameter() + " from " +
                ctx.getChannel().getRemoteAddress());

        String hashString = generateParameterHashString(message.getParameter());
        ProtocolMessage responseMessage =
                ProtocolMessageFactory.createResponse(hashString);

        sendProtocolMessage(ctx, responseMessage);

    }

    public void sendProtocolMessage(ChannelHandlerContext ctx,
                                    ProtocolMessage message) {

        // Sends a ProtocolMessage to next layer and after successful transfer
        // it will close the connection. In this case the next layer is the
        // ProtocolHandler

        ChannelFuture channelFuture = ctx.getChannel().write(message);

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                Channel channel = future.getChannel();
                channel.close();
            }
        });
    }

    private String generateParameterHashString(String parameter) {

        // Return a hash as string of a given parameter using
        // Googles guava MD5 Hashing.
        //
        // For detailed information see:
        // https://code.google.com/p/guava-libraries/wiki/HashingExplained

        return this.hashFunction.hashString(parameter).toString();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

        // Received messages from above layer, in this case ProtocolHandler
        // If received message is really a ProtocolMessage forward it to our
        // business logic

        if (e.getMessage() instanceof ProtocolMessage) {
            protocolMessageReceived(ctx, (ProtocolMessage) e.getMessage());
        }
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        log.warn("IdleAlarm! Closing channel " +
                e.getChannel().getRemoteAddress());
        e.getChannel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.warn("Unexpected exception", e.getCause());
        e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx,
                                 ChannelStateEvent e) {
        log.debug("Connected to: " +
                ctx.getChannel().getRemoteAddress().toString());
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        log.debug("Channel closed: " +
                ctx.getChannel().getRemoteAddress().toString());
    }
}
