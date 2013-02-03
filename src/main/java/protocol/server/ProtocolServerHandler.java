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

    private void protocolMessageReceived(ChannelHandlerContext ctx,
                                        ProtocolMessage message) {

        // This is our business logic. It receives a message and generate a hash
        // string of its parameter. This hash string will be send as a response
        // Message to our client. After successful transmitting or failure, the
        // connection will be closed.

        log.debug("Received message " + message.getParameter() + " from " +
                ctx.getChannel().getRemoteAddress());

        String hashString = generateParameterHashString(message.getParameter());
        ProtocolMessage responseMessage =
                ProtocolMessageFactory.createResponse(hashString);

        ChannelFuture channelFuture = sendProtocolMessage(ctx, responseMessage);
        channelFuture.addListener(ChannelFutureListener.CLOSE);
        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    private ChannelFuture sendProtocolMessage(ChannelHandlerContext ctx,
                                    ProtocolMessage message) {

        // Forward a ProtocolMessage to the next layer. In our case it is the
        // ProtocolHandler

        return ctx.getChannel().write(message);
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

        // Received messages from lower layer, in this case ProtocolHandler
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
        log.warn("Unexpected exception", e.getCause().getMessage());
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
