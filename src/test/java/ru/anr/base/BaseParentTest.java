/**
 * 
 */
package ru.anr.base;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

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
}
