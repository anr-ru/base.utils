/**
 * 
 */
package ru.anr.base;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseParent tests
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */

public class BaseParentTest extends BaseParent {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseParentTest.class);

    /**
     * String for tests
     */
    private static final String TEST_STRING = "T123T";

    /**
     * Test method for {@link ru.anr.base.BaseParent#list(Object...)}.
     */
    @Test
    public void testList() {

        List<String> l = list("x", "y");
        Assert.assertEquals(2, l.size());

        Assert.assertTrue(l.contains("x"));
        Assert.assertTrue(l.contains("y"));

        /*
         * Boundary cases
         */
        l = list();
        Assert.assertTrue(l.isEmpty());
        l = list((String) null);

        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(null));

        /*
         * test null safe create
         */
        l = list("x", "y");
        Assert.assertEquals(l, list(l));
        l = null;
        Assert.assertNotNull(list(l));
    }

    /**
     * Test method for {@link ru.anr.base.BaseParent#set(Object...)}.
     */
    @Test
    public void testSet() {

        Set<String> l = set("x", "y");
        Assert.assertEquals(2, l.size());

        Assert.assertTrue(l.contains("x"));
        Assert.assertTrue(l.contains("y"));

        /*
         * Boundary cases
         */
        l = set();
        Assert.assertTrue(l.isEmpty());
        l = set((String) null);

        Assert.assertEquals(1, l.size());
        Assert.assertTrue(l.contains(null));
    }

    /**
     * Test method for
     * {@link ru.anr.base.BaseParent#concat(Object[], Object...)}.
     */
    @Test
    public void testConcat() {

        String[] r = concat(new String[]{ "x", "y" }, "z");
        Assert.assertEquals(3, r.length);

        Set<String> l = set(r);
        Assert.assertTrue(l.contains("x"));
        Assert.assertTrue(l.contains("y"));
        Assert.assertTrue(l.contains("z"));

        /*
         * 1. Boundary cases
         */
        r = concat(new String[]{ "x", "y" });
        Assert.assertEquals(2, r.length);

        l = set(r);
        Assert.assertTrue(l.contains("x"));
        Assert.assertTrue(l.contains("y"));

        /*
         * 2.
         */
        r = concat(null, "x");
        Assert.assertEquals(1, r.length);

        l = set(r);
        Assert.assertTrue(l.contains("x"));

        /*
         * 3.
         */
        r = concat(null);
        Assert.assertEquals(0, r.length);

        l = set(r);
        Assert.assertEquals(0, l.size());
    }

    /**
     * Tests for inject method
     */
    @Test
    public void testInject() {

        SampleObject o = new SampleObject(null, 0);
        Assert.assertNull(o.getValue());

        inject(o, "value", "xxx");

        Assert.assertNotNull(o.getValue());
    }

    /**
     * Tests for the 'first' method
     */
    @Test
    public void testFirst() {

        List<String> l = list();
        Assert.assertNull(first(l));
        Assert.assertNull(first((Set<?>) null));

        l = list("x", "y");
        Assert.assertEquals("x", first(l));

        Set<String> s = set();
        Assert.assertNull(first(s));

        s = set("x", "y");
        Assert.assertEquals("x", first(s));
    }

    /**
     * Tests for the 'first for a stream' method
     */
    @Test
    public void testFirstForStream() {

        List<String> l = list();
        Assert.assertNull(first(l.stream()));
        Assert.assertNull(first((Stream<String>) null));

        l = list("x", "y");
        Assert.assertEquals("x", first(l.stream()));

        Set<String> s = set();
        Assert.assertNull(first(s.stream()));

        s = set("x", "y");
        Assert.assertEquals("x", first(s.stream()));

    }

    /**
     * Tests for get method
     */
    @Test
    public void testFilter() {

        List<String> list = list("xx", "xy", "yy");
        Assert.assertEquals(list("xx", "xy"), filter(list, p -> p.startsWith("x")));
        Assert.assertEquals(list("yy"), filter(list, p -> p.startsWith("y")));

        Assert.assertEquals(list(), filter(list, p -> p.startsWith("z"))); // empty

        // Trying more complex algorithm
        Predicate<String> predicate = p -> {

            if (p.startsWith("x")) {
                logger.info("found x: {}", p);
                return p.endsWith("y");
            }
            return false;
        };

        Assert.assertEquals(list("xy"), filter(list, predicate));
    }

    /**
     * Testing {@link BaseParent#toMap(Object[])}
     */
    @Test
    public void testValues() {

        /*
         * Even number of elements
         */
        String[] values = new String[]{ "x", "1", "y", "2" };

        Map<String, String> map = toMap(values);

        logger.debug("Map: {}", map);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals("1", map.get("x"));
        Assert.assertEquals("2", map.get("y"));

        /*
         * Odd number of elements
         */
        values = new String[]{ "x", "1", "y" };

        map = toMap(values);
        logger.debug("Map: {}", map);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals("1", map.get("x"));
        Assert.assertEquals(null, map.get("y"));

        /*
         * Array is null => let the map be empty for convience
         */
        values = null;

        map = toMap(values);
        logger.debug("Map: {}", map);

        Assert.assertEquals(0, map.size());

        /*
         * Array is empty
         */
        values = new String[]{};

        map = toMap(values);
        logger.debug("Map: {}", map);

        Assert.assertEquals(0, map.size());

        /*
         * One element array of Integer
         */
        Integer[] ints = new Integer[]{ 64 };

        Map<String, Integer> mapi = toMap(ints);
        logger.debug("Map: {}", mapi);

        Assert.assertEquals(1, mapi.size());
        Assert.assertEquals(null, map.get("64"));

        /*
         * Different type elements
         */
        Object[] objs = new Object[]{ "x", 64, "y", true, "z" };

        Map<String, Object> mapx = toMap(objs);
        logger.debug("Map: {}", mapx);

        Assert.assertEquals(3, mapx.size());
        Assert.assertEquals(64, mapx.get("x"));
        Assert.assertEquals(true, mapx.get("y"));
        Assert.assertEquals(null, mapx.get("z"));

        /*
         * Lots of elements
         */
        values = new String[]{ "x", "1", "y", "2", "z", "3", "w", "4", "v", "5" };
        map = toMap(values);

        logger.debug("Map: {}", map);

        Assert.assertEquals(5, map.size());
        Assert.assertEquals("1", map.get("x"));
        Assert.assertEquals("2", map.get("y"));
        Assert.assertEquals("3", map.get("z"));
        Assert.assertEquals("4", map.get("w"));
        Assert.assertEquals("5", map.get("v"));

        /*
         * Strange cases
         */
        values = new String[]{ null, "1", "y", "2" };
        map = toMap(values);

        logger.debug("Map: {}", map);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals("1", map.get(null));
        Assert.assertEquals("2", map.get("y"));
    }

    /**
     * Parsing
     */
    @Test
    public void parsing() {

        Integer v = parse("64", Integer.class);
        Assert.assertNotNull(v);
        Assert.assertEquals(64, v.intValue());

        v = parse("64f", Integer.class);
        Assert.assertNull(v);

        BigDecimal x = parse("64", BigDecimal.class);
        Assert.assertNotNull(x);
        Assert.assertEquals(new BigDecimal("64"), x);

        x = parse("64/f", BigDecimal.class);
        Assert.assertNull(x);

        Long id = parse(null, Long.class);
        Assert.assertNull(id);
    }

    /**
     * test bytes
     */
    @Test
    public void testBytes() {

        byte[] b = utf8(TEST_STRING);
        String s1 = utf8(b);
        Assert.assertEquals(TEST_STRING, s1);
    }

    /**
     * test nullSafe String
     */
    @Test
    public void testNullSafe() {

        String s = null;
        Assert.assertNotNull(nullSafe(s));
        s = TEST_STRING;
        Assert.assertEquals(TEST_STRING, nullSafe(s));
    }

    /**
     * test guid
     */
    @Test
    public void testGuid() {

        String s = guid();
        Assert.assertNotNull(s);
        Assert.assertEquals(36, s.length());
    }

    /**
     * test Clone
     */
    @Test
    public void testClone() {

        Calendar calendar = Calendar.getInstance();
        cloneObject(calendar);
        Assert.assertEquals(calendar, cloneObject(calendar));
    }

    /**
     * test nullSafe String
     */
    @Test
    public void testDate() {

        ZonedDateTime zdt = now();
        Date date = date(zdt);
        Assert.assertNotNull(date);
        sleep(200);
        Assert.assertTrue(date.before(date(now())));
    }

    /**
     * test safeEquals
     */
    @Test
    public void testSafeEquals() {

        String s = null;
        Assert.assertFalse(safeEquals(TEST_STRING, s));
        Assert.assertFalse(safeEquals(s, TEST_STRING));
        Assert.assertTrue(safeEquals(TEST_STRING, TEST_STRING));
    }

    /**
     * Mapper tests
     */
    @Test
    public void testListToMap() {

        List<SampleObject> list = list(new SampleObject("xxx", 1), new SampleObject("yyy", 2));
        Map<String, Integer> map = toMap(list, SampleObject::getValue, SampleObject::getIndex);

        Assert.assertEquals(2, map.size());
        Assert.assertEquals(1, map.get("xxx").intValue());
        Assert.assertEquals(2, map.get("yyy").intValue());
    }

    /**
     * Mapping with merging tests
     */
    @Test
    public void testListToMapWithMerging() {

        List<SampleObject> list = list(new SampleObject("xx", 1), new SampleObject("yy", 2), new SampleObject("xx", 5),
                new SampleObject("yy", 8), new SampleObject("xx", 32));

        Map<String, Integer> map = toMap(list, SampleObject::getValue, SampleObject::getIndex, (v1, v2) -> v1 + v2);

        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey("xx"));
        Assert.assertTrue(map.containsKey("yy"));
        Assert.assertEquals(2 + 8, map.get("yy").intValue());
        Assert.assertEquals(1 + 5 + 32, map.get("xx").intValue());

        // Boundary cases
        list = list();
        map = toMap(list, SampleObject::getValue, SampleObject::getIndex, (v1, v2) -> v1 + v2);

        Assert.assertEquals(0, map.size());
    }

}
