
package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ParseUtils}
 *
 * @author Alexey Romanchuk
 * @created Apr 19, 2016
 *
 */

public class ParseUtilsTest extends BaseParent {

    /**
     * Tests for XML xpath queries
     */
    @Test
    public void xmlParser() {

        String xml = "<a b=\"123\"><b><c>1</c><c>2</c></b></a>";

        Assertions.assertEquals("123", ParseUtils.xpath(xml, "//@b"));
        Assertions.assertEquals("1", ParseUtils.xpath(xml, "//c[1]"));
        Assertions.assertEquals("2", ParseUtils.xpath(xml, "//c[2]"));
        Assertions.assertEquals("12", ParseUtils.xpath(xml, "/a/b"));

        Assertions.assertEquals("", ParseUtils.xpath(xml, "//d"));
    }

    /**
     * Tests for regular expressions
     */
    @Test
    public void regexpParser() {

        String s = "<a b=\"123\"><b><c>1</c><c>2</c></b></a>";

        Assertions.assertEquals("123", ParseUtils.regexp(s, "b=\"(\\d+)\"", 1));
        Assertions.assertEquals("1", ParseUtils.regexp(s, "<c>(\\d+)</c><c>", 1));
        Assertions.assertEquals("2", ParseUtils.regexp(s, "</c><c>(\\d+)</c>", 1));
        Assertions.assertEquals("12", ParseUtils.regexp(s, "<c>(\\d+)</c><c>(\\d+)</c>", 1, 2));

        Assertions.assertNull(ParseUtils.regexp(s, "xxx", 1));

        Assertions.assertEquals(list("1", "2"), ParseUtils.regexpGroups(s, "<c>(\\d+)</c><c>(\\d+)</c>", 1, 2));
        Assertions.assertEquals(list("1"), ParseUtils.regexpGroups(s, "<c>(\\d+)</c>(<c>\\d[34]</c>)?", 1, 2));
    }
}
