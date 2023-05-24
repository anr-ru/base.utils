package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import java.time.LocalDate;
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
    public void xmlParser() throws TransformerException {

        // I. No namespace
        String xml = "<a b=\"123\">" +
                "<b>" +
                "<c>1</c>" +
                "<c>2</c>" +
                "</b>" +
                "</a>";

        // 1. Select as strings
        Assertions.assertEquals("123", xpath(xml, "//@b"));
        Assertions.assertEquals("1", xpath(xml, "//c[1]"));
        Assertions.assertEquals("2", xpath(xml, "//c[2]"));
        Assertions.assertEquals("12", xpath(xml, "/a/b"));
        Assertions.assertEquals("", xpath(xml, "//d"));

        // 2. Select as nodes
        NodeList nodes = ParseUtils.xpath(xml, "/a/b/c", XPathConstants.NODESET);
        Assertions.assertEquals(2, nodes.getLength());


        // II. No namespace
        xml = "<x:a xmlns:x=\"NAMESPACE\" b=\"123\">" +
                "<b>" +
                "<c>1</c>" +
                "<c>2</c>" +
                "</b>" +
                "</x:a>";

        // 1. Select as strings
        Assertions.assertEquals("123", xpath(xml, "//@b"));
        Assertions.assertEquals("1", xpath(xml, "//c[1]"));
        Assertions.assertEquals("2", xpath(xml, "//c[2]"));
        Assertions.assertEquals("12", ParseUtils.xpath(xml, "/x:a/b",
                XPathConstants.STRING, ParseUtils.namespaceResolver("x", "NAMESPACE")));
        Assertions.assertEquals("", xpath(xml, "//d"));

        // 2. Select as nodes
        nodes = ParseUtils.xpath(xml, "/x:a/b/c",
                XPathConstants.NODESET, ParseUtils.namespaceResolver("x", "NAMESPACE"));
        Assertions.assertEquals(2, nodes.getLength());

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
        // Date and time
        Assertions.assertEquals(LocalDateTime.of(2020, 5, 14, 0, 0),
                parseLocal("2020-05-14", "yyyy-MM-dd").orElse(null));
        Assertions.assertEquals(LocalDateTime.of(2020, 5, 14, 5, 12),
                parseLocal("2020-05-14 05:12", "yyyy-MM-dd HH:mm").orElse(null));

        // Date
        Assertions.assertEquals(LocalDate.of(2020, 5, 14),
                parseLocalDate("2020-05-14", "yyyy-MM-dd").orElse(null));
        Assertions.assertEquals(LocalDate.of(2020, 5, 14),
                parseLocalDate("14.05.2020", "dd.MM.yyyy").orElse(null));
        Assertions.assertEquals(LocalDate.of(2020, 5, 14),
                parseLocalDate("14.05.20", "dd.MM.yy").orElse(null));
    }

    @Test()
    public void parseWrongDate() {
        Assertions.assertNull(ParseUtils.parseLocal("2020-05-14 0x:12", "yyyy-MM-dd"));
        Assertions.assertNull(ParseUtils.parseLocal("2020-xx-14 05:12", "yyyy-MM-dd"));
    }
}
