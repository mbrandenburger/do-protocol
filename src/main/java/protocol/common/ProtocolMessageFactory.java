package protocol.common;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/2/13
 * Time: 1:01 AM
 */
public class ProtocolMessageFactory {

    // TODO create additional factory methods
    // (e.g. ProtocolMessage with header)

    public static ProtocolMessage createBadProtocolMessage(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.BAD_REQUEST,
                parameter);
    }

    public static ProtocolMessage createResponse(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.RESPONSE,
                parameter);
    }

    public static ProtocolMessage createRequest(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.REQUEST,
                parameter);
    }
}
