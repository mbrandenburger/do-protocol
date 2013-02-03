package protocol.common;

import org.jboss.netty.channel.*;
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
public class ProtocolHandler extends SimpleChannelHandler {

    private static final org.slf4j.Logger log =
            LoggerFactory.getLogger(ProtocolHandler.class);

    private static final Pattern FIRST_LINE_REGEX =
            Pattern.compile("DO\\s+([\\w/_-]+)");
    private static final Pattern HEADER_REGEX =
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
                this.bufferMessage =
                        ProtocolMessageFactory.createRequest(matcher.group(1));
            } else {
                // first line does not match expression
                protocolError(ctx, "Invalid protocol exception");
            }
        } else {
            // read header line
            String headerLine = (String) e.getMessage();

            if (headerLine.isEmpty()) {
                // end of bufferMessage
                fireMessageReceived(ctx, resetBufferMessage());
            } else {
                Matcher matcher = HEADER_REGEX.matcher(headerLine);
                if (matcher.matches()) {
                    this.bufferMessage
                            .addHeader(matcher.group(1), matcher.group(2));
                } else {
                    // line does not match expression
                    protocolError(ctx, "Invalid header format exception");
                }
            }
        }
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

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

    private ProtocolMessage resetBufferMessage() {
        ProtocolMessage message = this.bufferMessage;
        this.bufferMessage = null;
        return message;
    }

    private void protocolError(ChannelHandlerContext ctx, String parameter) {

        ProtocolMessage errorMessage =
                ProtocolMessageFactory.createBadProtocolMessage(parameter);

        ChannelFuture channelFuture = ctx.getChannel().write(errorMessage);

        channelFuture.addListener(ChannelFutureListener.CLOSE);
        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

}