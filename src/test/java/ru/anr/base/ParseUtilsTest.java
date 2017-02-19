/**
 * 
 */
package ru.anr.base;

import org.junit.Assert;
import org.junit.Test;

/**
 * Description ...
 *
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

        Assert.assertEquals("123", ParseUtils.xpath(xml, "//@b"));
        Assert.assertEquals("1", ParseUtils.xpath(xml, "//c[1]"));
        Assert.assertEquals("2", ParseUtils.xpath(xml, "//c[2]"));
        Assert.assertEquals("12", ParseUtils.xpath(xml, "/a/b"));

        Assert.assertEquals("", ParseUtils.xpath(xml, "//d"));
    }

    /**
     * Tests for regular expressions
     */
    @Test
    public void regexpParser() {

        String s = "<a b=\"123\"><b><c>1</c><c>2</c></b></a>";

        Assert.assertEquals("123", ParseUtils.regexp(s, "b=\"(\\d+)\"", 1));
        Assert.assertEquals("1", ParseUtils.regexp(s, "<c>(\\d+)</c><c>", 1));
        Assert.assertEquals("2", ParseUtils.regexp(s, "</c><c>(\\d+)</c>", 1));
        Assert.assertEquals("12", ParseUtils.regexp(s, "<c>(\\d+)</c><c>(\\d+)</c>", 1, 2));

        Assert.assertNull(ParseUtils.regexp(s, "xxx", 1));

        Assert.assertEquals(list("1", "2"), ParseUtils.regexpGroups(s, "<c>(\\d+)</c><c>(\\d+)</c>", 1, 2));
        Assert.assertEquals(list("1"), ParseUtils.regexpGroups(s, "<c>(\\d+)</c>(<c>\\d[34]</c>)?", 1, 2));

    }
}
