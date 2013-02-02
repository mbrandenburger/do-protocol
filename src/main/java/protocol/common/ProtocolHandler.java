package protocol.common;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.*;
import org.slf4j.LoggerFactory;

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

    //    static final String FIRST_LINE_REGEX = "DO\\s{1}[\\w/_-]*";
    //    static final String HEADER_REGEX = "[\\w/_-]*:\\s{1}[\\w-]*";
    public static final String FIRST_LINE_REGEX = "DO\\s[\\w/_-]*";
    public static final String HEADER_REGEX = "[\\w/_-]*:\\s[\\w-]*";

    private ProtocolMessage bufferMessage;

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

        if (this.bufferMessage == null) {
            // read initial
            String firstLine = (String) e.getMessage();
            if (firstLine.matches(FIRST_LINE_REGEX)) {
                createBufferMessage(firstLine);
            } else {
                protocolError(ctx, e, "InvalidFormatException");
            }
        } else {
            // read header line
            String headerLine = (String) e.getMessage();
            // end of bufferMessage
            if (headerLine.isEmpty()) {
                fireMessageReceived(ctx, resetBufferMessage());
            } else {
                if (headerLine.matches(HEADER_REGEX)) {
                    addHeader(headerLine);
                } else {
                    protocolError(ctx, e, "InvalidFormatException");
                }
            }
        }
    }

    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {

        if (e.getMessage() instanceof ProtocolMessage) {
            ProtocolMessage message = (ProtocolMessage) e.getMessage();
            log.debug("Send " + message + " to " +
                    ctx.getChannel().getRemoteAddress());

            DownstreamMessageEvent dme =
                    new DownstreamMessageEvent(e.getChannel(), e.getFuture(),
                            message.toStringMessage(), e.getRemoteAddress());

            ctx.sendDownstream(dme);
        }
    }

    private ProtocolMessage resetBufferMessage() {
        ProtocolMessage message = this.bufferMessage;
        this.bufferMessage = null;
        return message;
    }

    private ProtocolMessage createBufferMessage(String parameter) {
        this.bufferMessage = ProtocolMessageFactory
                .createRequest(StringUtils.substring(parameter, 3));
        return this.bufferMessage;
    }

    private void addHeader(String headerLine) {
        // TODO validate headerLine before split and add
        String[] keyValue = headerLine.split(": ");
        this.bufferMessage.addHeader(keyValue[0], keyValue[1]);
    }

    private void protocolError(ChannelHandlerContext ctx, MessageEvent e,
                               String parameter) {
        ProtocolMessage errorMessage =
                ProtocolMessageFactory.createBadProtocolMessage(parameter);

        ChannelFuture channelFuture = ctx.getChannel().write(errorMessage);

        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                Channel channel = future.getChannel();
                channel.close();
            }
        });
    }

}