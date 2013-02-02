package protocol.common;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/2/13
 * Time: 1:01 AM
 */
public class ProtocolMessageFactory {

    static ProtocolMessage createBadProtocolMessage(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.BAD_REQUEST,
                parameter);
    }

    static ProtocolMessage createResponse(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.RESPONSE,
                parameter);
    }

    static ProtocolMessage createRequest(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.REQUEST,
                parameter);
    }
}
