package protocol.common;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/1/13
 * Time: 8:40 PM
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


    protected MessageType type = null;
    protected String parameter = null;
    protected Map<String, String> headers = null;

    public ProtocolMessage(MessageType type, String parameter) {
        this.type = type;
        this.parameter = parameter;
        this.headers = new HashMap<String, String>();
    }

    public MessageType getType() {
        return type;
    }

    public String getTypeAsString() {
        return type.getValue();
    }

    public String getParameter() {
        return this.parameter;
    }

    // If key already exists, the old value is replaced.
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return ImmutableMap.copyOf(this.headers);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && getClass() != obj.getClass()) {
            return false;
        }

        final ProtocolMessage other = (ProtocolMessage) obj;

        // TODO for simplicity at this point only message parameter compared
        return Objects.equal(this.parameter, other.parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.parameter, this.headers);
    }

    @Override
    public String toString() {
        Objects.ToStringHelper stringHelper =
                Objects.toStringHelper(this).addValue(this.parameter);

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            stringHelper.addValue(entry.toString());
        }
        return stringHelper.toString();
    }

    public String toStringMessage() {
        StringBuilder stringMessageBuilder =
                new StringBuilder().append(this.getTypeAsString()).append(" ")
                        .append(this.parameter).append("\n");

        if (!this.headers.isEmpty()) {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                stringMessageBuilder.append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append("\n");
            }
            stringMessageBuilder.append("\n");
        }

        return stringMessageBuilder.toString();
    }


}
