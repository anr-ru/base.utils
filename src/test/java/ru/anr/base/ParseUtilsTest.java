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

        Assertions.assertEquals("123", xpath(xml, "//@b"));
        Assertions.assertEquals("1", xpath(xml, "//c[1]"));
        Assertions.assertEquals("2", xpath(xml, "//c[2]"));
        Assertions.assertEquals("12", xpath(xml, "/a/b"));
        Assertions.assertEquals("", xpath(xml, "//d"));
    }

    /**
     * Tests for regular expressions
     */
    @Test
    public void regexpParser() {

        String s = "<a b=\"123\"><b><c>1</c><c>2</c></b></a>";

        Assertions.assertEquals("123", regexp(s, "b=\"(\\d+)\"", 1));
        Assertions.assertEquals("1", regexp(s, "<c>(\\d+)</c><c>", 1));
        Assertions.assertEquals("2", regexp(s, "</c><c>(\\d+)</c>", 1));
        Assertions.assertEquals("12", regexp(s, "<c>(\\d+)</c><c>(\\d+)</c>", 1, 2));

        Assertions.assertNull(regexp(s, "xxx", 1));

        Assertions.assertEquals(list("1", "2"), regexpGroups(s, "<c>(\\d+)</c><c>(\\d+)</c>", 1, 2));
        Assertions.assertEquals(list("1"), regexpGroups(s, "<c>(\\d+)</c>(<c>\\d[34]</c>)?", 1, 2));
    }

    private enum XXXEnum {
        X, Y
    }

    /**
     * Tests for regular expressions
     */
    @Test
    public void parseEnum() {

        Assertions.assertEquals(XXXEnum.X, parseEnum(XXXEnum.class, "X"));
        Assertions.assertEquals(XXXEnum.Y, parseEnum(XXXEnum.class, "Y"));
        Assertions.assertNull(parseEnum(XXXEnum.class, null));
        Assertions.assertNull(parseEnum(XXXEnum.class, "Z"));
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
