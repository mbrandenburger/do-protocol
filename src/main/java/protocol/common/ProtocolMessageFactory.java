package protocol.common;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/2/13
 * Time: 1:01 AM
 */
public class ProtocolMessageFactory {

    // Not a real factory pattern yet, but could be helpful if protocol
    // message complexity increases
    //
    // additional factory methods might be help (e.g. ProtocolMessage with
    // header)

    /**
     * Create a protocol message with type BAD_REQUEST
     * @param parameter the message parameter
     * @return          a ProtocolMessage with
     */
    public static ProtocolMessage createBadProtocolMessage(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.BAD_REQUEST,
                parameter);
    }

    /**
     * Create a protocol message with type RESPONSE
     * @param parameter
     * @return
     */
    public static ProtocolMessage createResponse(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.RESPONSE,
                parameter);
    }

    /**
     * Create a protocol message with type REQUEST
     * @param parameter
     * @return
     */
    public static ProtocolMessage createRequest(String parameter) {
        return new ProtocolMessage(ProtocolMessage.MessageType.REQUEST,
                parameter);
    }
}
