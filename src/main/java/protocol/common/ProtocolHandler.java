package protocol.common;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/1/13
 * Time: 11:47 PM
 */
public class ProtocolHandler extends IdleStateAwareChannelHandler {

    private static final org.slf4j.Logger log =
            LoggerFactory.getLogger(ProtocolHandler.class);

    public static final Pattern FIRST_LINE_REGEX =
            Pattern.compile("(DO)\\s+([\\w/_-]+)");
    public static final Pattern HEADER_REGEX =
            Pattern.compile("([\\w-]+):\\s+([\\w/_-]+)");

    private ProtocolMessage bufferMessage;

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

        // This is our protocol message decoder. It tries to decode each
        // received line and put it into a ProtocolMessage object.
        //
        // If we have no bufferMessage, we would assume that this
        // incoming message is the first line of a protocol message. Otherwise,
        // this message is treated as a header.
        //
        // Each line will be verified with a appropriated regular expression
        // pattern. In case of a mismatch, a BadRequest message will be sent and
        // we close the connection.
        //
        // If we have a bufferMessage and receive a empty line, the
        // bufferMessage will be forwarded to the next layer, in our case the
        // ProtocolServerHandler

        // Do not process any available message if channel was already closed.
        if (!ctx.getChannel().isConnected()) {
            return;
        }

        if (this.bufferMessage == null) {
            Matcher matcher = FIRST_LINE_REGEX.matcher((String) e.getMessage());
            if (matcher.matches()) {
                // group(1) = DO
                // group(2) = Paramter
                this.bufferMessage =
                        ProtocolMessageFactory.createRequest(matcher.group(2));
            } else {
                // first line does not match expression
                protocolError(ctx, "InvalidProtocolException");
            }
        } else {
            // read header line
            String headerLine = (String) e.getMessage();

            if (headerLine.isEmpty()) {
                // end of bufferMessage
                fireMessageReceived(ctx, resetBufferMessage());
            } else {
                Matcher matcher = HEADER_REGEX.matcher(headerLine);
                // TODO what will happen if client sends infinitive amount of
                // header lines? For that case we should count headers and stop
                // reading if maximum reaches we will send error message close
                // connection
                if (matcher.matches()) {
                    // group(1) = Key
                    // group(2) = Value
                    this.bufferMessage
                            .addHeader(matcher.group(1), matcher.group(2));
                } else {
                    // line does not match expression
                    protocolError(ctx, "InvalidHeaderFormatException");
                }
            }
        }
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

        if(e.getMessage() == null ){
            log.debug("null msg");
        }

        // This is our protocol message encoder. It only forward
        // ProtocolMessages to the next Layer. In our case it is a
        // StringEncoder.

        // do not process messages if channel was closed
        if (!ctx.getChannel().isConnected()) {
            return;
        }

        if (e.getMessage() instanceof ProtocolMessage) {
            ProtocolMessage message = (ProtocolMessage) e.getMessage();
            log.debug("Send " + message.getParameter() + " to " +
                    ctx.getChannel().getRemoteAddress());

            DownstreamMessageEvent dme =
                    new DownstreamMessageEvent(e.getChannel(), e.getFuture(),
                            message.serialize(), e.getRemoteAddress());

            ctx.sendDownstream(dme);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.warn("Unexpected exception", e.getCause().getMessage());
        // What will happen if framer rise exception because more than 4096
        // character without \n in one line received. Framer would drop these
        // character and continue reading. In that case, the server should send
        // a bad protocol message to the client and close the connection.

        if( e.getCause() instanceof TooLongFrameException ) {
            protocolError(ctx, "TooLongFrameException");
        }
    }

    private ProtocolMessage resetBufferMessage() {
        ProtocolMessage message = this.bufferMessage;
        this.bufferMessage = null;
        return message;
    }

    private void protocolError(ChannelHandlerContext ctx, String parameter) {

        if (ctx.getChannel().isConnected()) {
            ProtocolMessage errorMessage =
                    ProtocolMessageFactory.createBadProtocolMessage(parameter);

            ChannelFuture channelFuture = ctx.getChannel().write(errorMessage);

            channelFuture.addListener(ChannelFutureListener.CLOSE);
            channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        log.warn("IdleAlarm! Closing channel " +
                e.getChannel().getRemoteAddress());
        protocolError(ctx, "TimeOutException");
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