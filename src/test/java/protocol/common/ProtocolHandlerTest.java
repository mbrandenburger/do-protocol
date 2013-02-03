package protocol.common;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 2/3/13
 * Time: 11:32 PM
 */
public class ProtocolHandlerTest extends TestCase {
    @Test
    public void testFirstLineRegex() throws Exception {

        Pattern pattern = ProtocolHandler.FIRST_LINE_REGEX;

        HashMap<String, Boolean> testStrings = new HashMap<String, Boolean>();
        testStrings.put("DO HalloWorld", Boolean.TRUE);
        testStrings.put("DO HeloWorld///___---1230", Boolean.TRUE);
        testStrings.put("DO  Hello", Boolean.TRUE);
        testStrings.put("DO  Hello ", Boolean.FALSE);
        testStrings.put("DO !Hello", Boolean.FALSE);
        testStrings.put(" DO !Hello", Boolean.FALSE);
        testStrings.put("DOW Hello", Boolean.FALSE);
        testStrings.put("DOHello", Boolean.FALSE);
        testStrings.put("DO", Boolean.FALSE);
        testStrings.put("", Boolean.FALSE);

        for (Map.Entry<String, Boolean> entry : testStrings.entrySet())
            assertEquals(entry.getValue().booleanValue(),
                    pattern.matcher(entry.getKey()).matches());
    }

    @Test
    public void testHeaderRegex() throws Exception {

        Pattern pattern = ProtocolHandler.HEADER_REGEX;

        HashMap<String, Boolean> testStrings = new HashMap<String, Boolean>();
        testStrings.put("key: value", Boolean.TRUE);
        testStrings.put("DO: HeloWorld///___---1230", Boolean.TRUE);
        testStrings.put("DO: 123-124-123-asd--", Boolean.TRUE);
        testStrings.put("DO/_: 123-124-123-asd--", Boolean.FALSE);
        testStrings.put("0123das-123/_: 123-asd", Boolean.FALSE);
        testStrings.put("0123das-123/_ 123-asd", Boolean.FALSE);
        testStrings.put("0123das-123 123-asd", Boolean.FALSE);
        testStrings.put("012/_3das-123: 123-asd", Boolean.FALSE);
        testStrings.put("_: -", Boolean.TRUE);
        testStrings.put("-: _", Boolean.TRUE);
        testStrings.put("", Boolean.FALSE);
        testStrings.put(":", Boolean.FALSE);
        testStrings.put(": ", Boolean.FALSE);
        testStrings.put(" : ", Boolean.FALSE);

        for (Map.Entry<String, Boolean> entry : testStrings.entrySet())
            assertEquals(entry.getValue().booleanValue(),
                    pattern.matcher(entry.getKey()).matches());
    }

}
