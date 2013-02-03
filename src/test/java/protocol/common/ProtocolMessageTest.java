package protocol.common;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/3/13
 * Time: 11:33 PM
 */
public class ProtocolMessageTest extends TestCase {
    @Test
    public void testGetType() throws Exception {

        for (ProtocolMessage.MessageType type : ProtocolMessage.MessageType
                .values()) {
            ProtocolMessage msg =
                    new ProtocolMessage(ProtocolMessage.MessageType.RESPONSE,
                            "test");
            assertEquals(ProtocolMessage.MessageType.RESPONSE, msg.getType());
        }
    }

    @Test
    public void testGetParameter() throws Exception {

        String testParam = "TestParam";
        ProtocolMessage msg =
                new ProtocolMessage(ProtocolMessage.MessageType.RESPONSE,
                        testParam);
        assertEquals(testParam, msg.getParameter());

    }

    @Test
    public void testGetHeaders() throws Exception {
        ProtocolMessage msg =
                new ProtocolMessage(ProtocolMessage.MessageType.REQUEST,
                        "hello");
        msg.addHeader("key", "value");

        Map<String, String> map = msg.getHeaders();

        assertEquals(true, map.containsKey("key"));
        assertEquals(true, map.containsValue("value"));
    }

    @Test
    public void testSerialize() throws Exception {
        ProtocolMessage msg;

        msg = new ProtocolMessage(ProtocolMessage.MessageType.REQUEST, "hello");
        assertEquals("DO hello\n\n", msg.serialize());

        msg = new ProtocolMessage(ProtocolMessage.MessageType.RESPONSE,
                "hello");
        assertEquals("DONE hello\n\n", msg.serialize());

        msg = new ProtocolMessage(ProtocolMessage.MessageType.BAD_REQUEST,
                "hello");
        assertEquals("BAD hello\n\n", msg.serialize());

        msg = new ProtocolMessage(ProtocolMessage.MessageType.REQUEST, "hello");
        msg.addHeader("key", "value");
        assertEquals("DO hello\nkey: value\n\n", msg.serialize());

        msg = new ProtocolMessage(ProtocolMessage.MessageType.REQUEST, "hello");
        msg.addHeader("key", "value");
        msg.addHeader("key", "value");
        assertEquals("DO hello\nkey: value\n\n", msg.serialize());

        msg = new ProtocolMessage(ProtocolMessage.MessageType.REQUEST, "hello");
        msg.addHeader("key", "value");
        assertEquals("DO hello\nkey: value\n\n", msg.serialize());

    }
}
