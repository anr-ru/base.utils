/**
 * 
 */
package ru.anr.base;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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
     * Test method for {@link ru.anr.base.BaseParent#list(S[])}.
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
    }

    /**
     * Test method for {@link ru.anr.base.BaseParent#set(S[])}.
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
     * Test method for {@link ru.anr.base.BaseParent#concat(S[], S[])}.
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

        SampleObject o = new SampleObject();
        Assert.assertNull(o.getValue());

        inject(o, "value", "xxx");

        Assert.assertNotNull(o.getValue());
    }

    /**
     * Tests for get method
     */
    @Test
    public void testGet() {

        List<String> l = list();
        Assert.assertNull(get(l));
        Assert.assertNull(get(null));

        l = list("x", "y");
        Assert.assertEquals("x", get(l));

        Set<String> s = set();
        Assert.assertNull(get(s));

        s = set("x", "y");
        Assert.assertEquals("x", get(s));
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
}
