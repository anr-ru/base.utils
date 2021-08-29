package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * Tests for {@link ParseUtils}
 *
 * @author Alexey Romanchuk
 * @created Apr 19, 2016
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

    private enum XXXEnum {
        X, Y
    }

    /**
     * Tests for regular expressions
     */
    @Test
    public void parseEnum() {

        Assertions.assertEquals(XXXEnum.X, ParseUtils.parseEnum(XXXEnum.class, "X"));
        Assertions.assertEquals(XXXEnum.Y, ParseUtils.parseEnum(XXXEnum.class, "Y"));
        Assertions.assertNull(ParseUtils.parseEnum(XXXEnum.class, null));
        Assertions.assertNull(ParseUtils.parseEnum(XXXEnum.class, "Z"));
    }

    /**
     * Tests for local date (without timezone) parser
     */
    @Test
    public void parseDate() {
        Assertions.assertEquals(LocalDateTime.of(2020, 5, 14, 0, 0),
                parseLocal("2020-05-14", "yyyy-MM-dd").orElse(null));
        Assertions.assertEquals(LocalDateTime.of(2020, 5, 14, 5, 12),
                parseLocal("2020-05-14 05:12", "yyyy-MM-dd HH:mm").orElse(null));
    }

    @Test()
    public void parseWrongDate() {
        Assertions.assertNull(ParseUtils.parseLocal("2020-05-14 0x:12", "yyyy-MM-dd"));
        Assertions.assertNull(ParseUtils.parseLocal("2020-xx-14 05:12", "yyyy-MM-dd"));
    }
}
