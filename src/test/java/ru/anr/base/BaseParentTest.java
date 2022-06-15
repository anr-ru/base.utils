package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * BaseParent tests
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 */

class BaseParentTest extends BaseParent {

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
    void testList() {

        List<String> l = list("x", "y");
        Assertions.assertEquals(2, l.size());

        Assertions.assertTrue(l.contains("x"));
        Assertions.assertTrue(l.contains("y"));

        /*
         * Boundary cases
         */
        l = list();
        Assertions.assertTrue(l.isEmpty());
        l = list((String) null);

        Assertions.assertEquals(1, l.size());
        Assertions.assertTrue(l.contains(null));

        /*
         * test null safe create
         */
        l = list("x", "y");
        Assertions.assertEquals(l, list(l));
        Assertions.assertNotNull(list());
    }

    /**
     * Test method for {@link ru.anr.base.BaseParent#list(List)}
     */
    @Test
    void testListByLisy() {

        List<String> l = list("x", "y");
        Assertions.assertEquals(list("x", "y"), list(l));

        Assertions.assertEquals(list(), list(new ArrayList<>()));
        Assertions.assertEquals(list(), list((List<String>) null));
    }

    /**
     * Test method for {@link ru.anr.base.BaseParent#set(Object...)}.
     */
    @Test
    void testSet() {

        Set<String> l = set("x", "y");
        Assertions.assertEquals(2, l.size());

        Assertions.assertTrue(l.contains("x"));
        Assertions.assertTrue(l.contains("y"));

        /*
         * Boundary cases
         */
        l = set();
        Assertions.assertTrue(l.isEmpty());

        l = set((String) null);
        Assertions.assertEquals(1, l.size());
        Assertions.assertTrue(l.contains(null));
    }

    /**
     * Test method for
     * {@link ru.anr.base.BaseParent#concat(Object[], Object...)}.
     */
    @Test
    void testConcat() {

        String[] r = concat(new String[]{"x", "y"}, "z");
        Assertions.assertEquals(3, r.length);

        Set<String> l = set(r);
        Assertions.assertTrue(l.contains("x"));
        Assertions.assertTrue(l.contains("y"));
        Assertions.assertTrue(l.contains("z"));

        /*
         * 1. Boundary cases
         */
        r = concat(new String[]{"x", "y"});
        Assertions.assertEquals(2, r.length);

        l = set(r);
        Assertions.assertTrue(l.contains("x"));
        Assertions.assertTrue(l.contains("y"));

        /*
         * 2.
         */
        r = concat(null, "x");
        Assertions.assertEquals(1, r.length);

        l = set(r);
        Assertions.assertTrue(l.contains("x"));

        /*
         * 3.
         */
        r = concat(null);
        Assertions.assertEquals(0, r.length);

        l = set(r);
        Assertions.assertEquals(0, l.size());
    }

    /**
     * Tests for inject method
     */
    @Test
    void testInject() {

        SampleObject o = new SampleObject(null, 0);
        Assertions.assertNull(o.getValue());

        inject(o, "value", "xxx");
        Assertions.assertEquals("xxx", o.getValue());
    }

    /**
     * Tests for the 'first' method
     */
    @Test
    void testFirst() {

        List<String> l = list();
        Assertions.assertNull(first(l));
        Assertions.assertNull(first((Set<?>) null));

        l = list("x", "y");
        Assertions.assertEquals("x", first(l));

        Set<String> s = set();
        Assertions.assertNull(first(s));

        s = set("x", "y");
        Assertions.assertEquals("x", first(s));
    }

    /**
     * Tests for the 'first for a stream' method
     */
    @Test
    void testFirstForStream() {

        List<String> l = list();
        Assertions.assertNull(first(l.stream()));
        Assertions.assertNull(first((Stream<String>) null));

        l = list("x", "y");
        Assertions.assertEquals("x", first(l.stream()));

        Set<String> s = set();
        Assertions.assertNull(first(s.stream()));

        s = set("x", "y");
        Assertions.assertEquals("x", first(s.stream()));

    }

    /**
     * Tests for get method
     */
    @Test
    void testFilter() {

        List<String> list = list("xx", "xy", "yy");
        Assertions.assertEquals(list("xx", "xy"), filter(list, p -> p.startsWith("x")));
        Assertions.assertEquals(list("yy"), filter(list, p -> p.startsWith("y")));

        Assertions.assertEquals(list(), filter(list, p -> p.startsWith("z"))); // empty

        // Trying more complex algorithm
        Predicate<String> predicate = p -> {

            if (p.startsWith("x")) {
                logger.debug("found x: {}", p);
                return p.endsWith("y");
            }
            return false;
        };

        Assertions.assertEquals(list("xy"), filter(list, predicate));

        // Null and empty
        Assertions.assertNull(filter(null, p -> true));
        Assertions.assertTrue(filter(list(), p -> true).isEmpty());
    }

    /**
     * Testing {@link BaseParent#toMap(Object[])}
     */
    @Test
    void testValues() {

        /*
         * Even number of elements
         */
        String[] values = new String[]{"x", "1", "y", "2"};

        Map<String, String> map = toMap(values);

        logger.debug("Map: {}", map);

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("1", map.get("x"));
        Assertions.assertEquals("2", map.get("y"));

        /*
         * Odd number of elements
         */
        values = new String[]{"x", "1", "y"};

        map = toMap(values);
        logger.debug("Map: {}", map);

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("1", map.get("x"));
        Assertions.assertNull(map.get("y"));

        /*
         * Array is null => let the map be empty for convience
         */
        map = toMap();
        logger.debug("Map: {}", map);

        Assertions.assertEquals(0, map.size());

        /*
         * Array is empty
         */
        values = new String[]{};

        map = toMap(values);
        logger.debug("Map: {}", map);

        Assertions.assertEquals(0, map.size());

        /*
         * One element array of Integer
         */
        Integer[] ints = new Integer[]{64};

        Map<String, Integer> mapi = toMap(ints);
        logger.debug("Map: {}", mapi);

        Assertions.assertEquals(1, mapi.size());
        Assertions.assertNull(map.get("64"));

        /*
         * Different type elements
         */
        Object[] objs = new Object[]{"x", 64, "y", true, "z"};

        Map<String, Object> mapx = toMap(objs);
        logger.debug("Map: {}", mapx);

        Assertions.assertEquals(3, mapx.size());
        Assertions.assertEquals(64, mapx.get("x"));
        Assertions.assertEquals(true, mapx.get("y"));
        Assertions.assertNull(mapx.get("z"));

        /*
         * Lots of elements
         */
        values = new String[]{"x", "1", "y", "2", "z", "3", "w", "4", "v", "5"};
        map = toMap(values);

        logger.debug("Map: {}", map);

        Assertions.assertEquals(5, map.size());
        Assertions.assertEquals("1", map.get("x"));
        Assertions.assertEquals("2", map.get("y"));
        Assertions.assertEquals("3", map.get("z"));
        Assertions.assertEquals("4", map.get("w"));
        Assertions.assertEquals("5", map.get("v"));

        /*
         * Strange cases
         */
        values = new String[]{null, "1", "y", "2"};
        map = toMap(values);

        logger.debug("Map: {}", map);

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals("1", map.get(null));
        Assertions.assertEquals("2", map.get("y"));
    }

    /**
     * Parsing
     */
    @Test
    void parsing() {

        Integer v = parse("64", Integer.class);
        Assertions.assertNotNull(v);
        Assertions.assertEquals(64, v.intValue());

        v = parse("64f", Integer.class);
        Assertions.assertNull(v);

        BigDecimal x = parse("64", BigDecimal.class);
        Assertions.assertNotNull(x);
        Assertions.assertEquals(new BigDecimal("64"), x);

        x = parse("64/f", BigDecimal.class);
        Assertions.assertNull(x);

        Long id = parse(null, Long.class);
        Assertions.assertNull(id);
    }

    /**
     * test bytes
     */
    @Test
    void testBytes() {

        byte[] b = utf8(TEST_STRING);
        String s1 = utf8(b);
        Assertions.assertEquals(TEST_STRING, s1);
    }

    /**
     * test nullSafe String
     */
    @Test
    void testNullSafe() {

        Assertions.assertNotNull(nullSafe(null));
        Assertions.assertEquals(TEST_STRING, nullSafe(TEST_STRING));
    }

    /**
     * test guid
     */
    @Test
    void testGuid() {

        String s = guid();
        Assertions.assertNotNull(s);
        Assertions.assertEquals(36, s.length());
    }

    /**
     * test Clone
     */
    @Test
    void testClone() {

        Calendar calendar = Calendar.getInstance();
        cloneObject(calendar);
        Assertions.assertEquals(calendar, cloneObject(calendar));
    }

    /**
     * test
     */
    @Test
    void testDate() {

        ZonedDateTime zdt = now();
        Date date = date(zdt);
        Assertions.assertNotNull(date);
        sleep(200);
        Assertions.assertTrue(date.before(date(now())));
    }

    /**
     * test safeEquals
     */
    @Test
    void testSafeEquals() {

        Assertions.assertFalse(safeEquals(TEST_STRING, null));
        Assertions.assertFalse(safeEquals(null, TEST_STRING));
        Assertions.assertTrue(safeEquals(TEST_STRING, TEST_STRING));
    }

    /**
     * Mapper tests
     */
    @Test
    void testListToMap() {

        List<SampleObject> list = list(new SampleObject("xxx", 1), new SampleObject("yyy", 2));
        Map<String, Integer> map = toMap(list, SampleObject::getValue, SampleObject::getIndex);

        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals(1, map.get("xxx").intValue());
        Assertions.assertEquals(2, map.get("yyy").intValue());
    }

    /**
     * Mapping with merging tests
     */
    @Test
    void testListToMapWithMerging() {

        List<SampleObject> list = list(new SampleObject("xx", 1), new SampleObject("yy", 2), new SampleObject("xx", 5),
                new SampleObject("yy", 8), new SampleObject("xx", 32));

        Map<String, Integer> map = toMap(list, SampleObject::getValue, SampleObject::getIndex, Integer::sum);

        Assertions.assertEquals(2, map.size());
        Assertions.assertTrue(map.containsKey("xx"));
        Assertions.assertTrue(map.containsKey("yy"));
        Assertions.assertEquals(2 + 8, map.get("yy").intValue());
        Assertions.assertEquals(1 + 5 + 32, map.get("xx").intValue());

        // Boundary cases
        list = list();
        map = toMap(list, SampleObject::getValue, SampleObject::getIndex, Integer::sum);

        Assertions.assertEquals(0, map.size());
    }

    /**
     * Test method for
     * {@link BaseParent#getEmptyKeys(java.util.Map, java.util.Collection)}
     */
    @Test
    void testGetEmptyKeys() {

        Map<String, String> map = new HashMap<>();
        map.put("a", "");
        map.put("b", "");
        map.put("c", "");
        Assertions.assertEquals(set("d"), getEmptyKeys(map, Arrays.asList("a", "b", "d")));
    }

    /**
     * Test method for {@link BaseParent#sha256(java.lang.String)}
     */
    @Test
    void testSha256() {

        String s1 = "string1";
        String s2 = "string2";
        Assertions.assertEquals(sha256(s1), sha256(s1));
        Assertions.assertNotEquals(sha256(s1), sha256(s2));

    }

    /**
     * Tests for
     * {@link BaseParent#extract(java.util.Collection, java.util.function.Function)}
     */
    @Test
    void testExtractKeys() {

        List<ZonedDateTime> dates = list(//
                ZonedDateTime.of(2017, 5, 12, 14, 15, 1, 0, DEFAULT_TIMEZONE),
                ZonedDateTime.of(2018, 5, 12, 14, 15, 1, 0, DEFAULT_TIMEZONE));

        Set<Integer> years = extract(dates, ZonedDateTime::getYear);

        Assertions.assertTrue(years.contains(2017));
        Assertions.assertTrue(years.contains(2018));
    }


    /*
     * Test for field() method
     */
    @Test
    public void testField() {

        SampleObject o = new SampleObject(null, -12);
        Assertions.assertNull(BaseParent.field(o, "value"));
        Assertions.assertEquals(Integer.valueOf(-12), BaseParent.field(o, "index"));

        o = new SampleObject("xxx", 1);
        Assertions.assertEquals("xxx", BaseParent.field(o, "value"));
        Assertions.assertEquals(Integer.valueOf(1), BaseParent.field(o, "index"));
    }

    /**
     * Testing private methods
     */
    @Test
    public void testMethod() {
        SampleObject x = new SampleObject("P", 0);
        SampleObject o = new SampleObject("POINT", -12);
        Assertions.assertEquals("X2POINT | SampleObject{value='P', index=0}", BaseParent.invoke(o, "run", 2, "X", x));
    }

    @Test
    public void nullSafe() {

        String s = "2";
        Assertions.assertEquals("23", nullSafe(s, x -> x + "3").orElse(null));

        Assertions.assertEquals("23", nullSafe(s, x -> x + "3").orElse("5"));

        Assertions.assertEquals("5", nullSafe(null, x -> x + "3").orElse("5"));
        nullSafe(null, x -> x + "3");
    }

    @Test
    public void addToCollection() {

        List<String> l = list("1", "2");
        Assertions.assertTrue(add(l, "3"));
        Assertions.assertEquals(list("1", "2", "3"), l);

        Assertions.assertEquals(list("1", "2", "3", "4"),
                safeAdd(l, "4"));
    }

    @Test
    public void nullSafeByClass() {

        SampleObject o = new SampleObject("2", 5);
        Assertions.assertEquals(o, nullSafe(o, SampleObject.class));

        SampleObject ox = nullSafe(null, SampleObject.class);
        Assertions.assertEquals(0, ox.getIndex());
        Assertions.assertNull(ox.getValue());
    }

    /**
     * Tests for {@link #d(String)} and {@link #d(double)}.
     */
    @Test
    public void testDecimal() {
        // Same scale
        Assertions.assertEquals(new BigDecimal("2.00"), d("2.00"));
        Assertions.assertEquals(new BigDecimal("5"), d("5"));

        // double
        Assertions.assertEquals(new BigDecimal("2.0"), d(2.00));
    }

    /**
     * Tests for {@link #scale(BigDecimal, int)}.
     */
    @Test
    public void testDecimalScale() {

        Assertions.assertEquals(new BigDecimal("2.00000"), scale(d("2.00"), 5));
    }

    /**
     * Tests for {@link #div(BigDecimal, BigDecimal, int)}.
     */
    @Test
    public void testDecimalDiv() {

        Assertions.assertEquals(d("2.00000"),
                div(d("12"), d("6"), 5));
    }

    /**
     * Tests for {@link #notEmpty(Object)} and {@link #isEmpty(Object)}.
     */
    @Test
    public void testEmptyObjects() {

        List<String> l = list("1", "2");

        Assertions.assertTrue(notEmpty(l));
        Assertions.assertFalse(isEmpty(l));

        Assertions.assertTrue(isEmpty(list()));
        Assertions.assertTrue(isEmpty(null));

        Assertions.assertFalse(notEmpty(list()));
        Assertions.assertFalse(notEmpty(null));

        Assertions.assertFalse(isEmpty("x"));
        Assertions.assertTrue(isEmpty(null));
        Assertions.assertTrue(isEmpty(""));
        Assertions.assertTrue(isEmpty(toMap()));
        Assertions.assertFalse(isEmpty(toMap("1", 2)));
    }

    @Test
    public void testContains() {

        List<String> l = list("1", "2");
        Assertions.assertTrue(contains(l, true, "1", "2"));
        Assertions.assertTrue(contains(l, false, "1"));
        Assertions.assertTrue(contains(l, false, "2"));
    }

    @Test
    public void testTotal() {
        List<BigDecimal> l = list(d("1"), d("-2"), d("3"));
        Assertions.assertEquals(d("6"), total(l, BigDecimal::abs));

        List<SampleObject> lx = list(
                new SampleObject("1", 0),
                new SampleObject("-2", 0),
                new SampleObject("3", 0));
        Assertions.assertEquals(d("2"), total(lx, SampleObject::asDecimal));
    }

    @Test
    public void testOfRunIgnored() {
        runIgnored(x -> {
            throw new NullPointerException("NPE");
        }, "1", "2");
        Assertions.assertEquals("OK", runIgnored(x -> "OK"));
    }

    @Test
    public void testWaitCondition() {
        Assertions.assertTrue(
                waitCondition("Text", 5, 200, true,
                        args -> false, "1", 2)
        );
        Assertions.assertFalse(
                waitCondition("Text", 5, 200, true,
                        args -> true, "1", 2)
        );

        Assertions.assertTrue(
                waitCondition("Text", 5, true,
                        args -> false, "1", 2)
        );
        Assertions.assertFalse(
                waitCondition("Text", 5, true,
                        args -> true, "1", 2)
        );

    }

    @Test
    public void testReadAsString() {
        Assertions.assertEquals("# Standard properties\n" +
                "spring.profiles.active=production\n" +
                "value=${DGC_VALUE}\n", readAsString("./application.properties"));
    }

    @Test
    public void abbreviate() {
        String s = "abcdefghijklmnopqrstuvwxyz";
        Assertions.assertEquals(s, abbreviate(s, 27));
        Assertions.assertEquals(s, abbreviate(s, 26));
        Assertions.assertEquals("abcdefghijklmnopqrstuvwxy", abbreviate(s, 25));
        Assertions.assertEquals("abcdefghij", abbreviate(s, 10));
        Assertions.assertEquals("a", abbreviate(s, 1));
        Assertions.assertEquals("", abbreviate(s, 0));
        Assertions.assertNull(abbreviate(null, 0));
    }
}
