package protocol.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/1/13
 * Time: 8:40 PM
 */

/**
 * A Protocol message which has a message type, a parameter and optional
 * headers. It provides a serialization method to meet given requirements.
 *
 */
public class ProtocolMessage {

    static enum MessageType {
        RESPONSE("DONE"),
        REQUEST("DO"),
        BAD_REQUEST("BAD");

        private final String value;

        private MessageType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final MessageType type;
    private final String parameter;
    private final Map<String, String> headers = new HashMap<String, String>();

    public ProtocolMessage(MessageType type, String parameter) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "'Message type' must not be null");
        }
        if (parameter == null) {
            throw new IllegalArgumentException(
                    "'Message parameter' must not be null");
        }

        // TODO validate parameter for [a-zA-Z0-9/_-]
        this.type = type;
        this.parameter = parameter;
    }

    /**
     * Returns message type.
     *
     * @see MessageType
     * @return message type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns message parameter. It is the parameter of the first line.
     *
     * @return message parameter as string
     */
    public String getParameter() {
        return this.parameter;
    }

    /**
     * Returns all message headers as unmodifiable map.
     *
     * @return a map of headers
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(this.headers);
    }

    /**
     * Adds a header a the protocol message.
     * If key already exists, the old value is replaced.
     *
     * @param key   a header key
     * @param value the associated value
     */
    public void addHeader(String key, String value) {
        // TODO validate key for [a-zA-Z0-9-] and value for [a-zA-Z0-9/_-]
        this.headers.put(key, value);
    }

    /**
     * Serializes the protocol message to a string as followed:
     *
     * TYPE Parameter\n
     * Key: Value\n
     * Key: Value\n
     * ...
     * \n
     *
     * @return serialized protocol message
     */
    public String serialize() {

        // To increase flexibility of this class we could parametrize the
        // header separator and line terminator

        StringBuilder sb = new StringBuilder();
        sb.append(this.getType().getValue());
        sb.append(" ");
        sb.append(this.parameter);
        sb.append("\n");

        if (!this.headers.isEmpty()) {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append("\n");
            }
        }
        sb.append("\n");

        return sb.toString();
    }

}
