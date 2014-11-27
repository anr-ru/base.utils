/**
 * 
 */
package ru.anr.base.tests;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.anr.base.BaseSpringParent;

/**
 * Tests for {@link BaseTestCase}
 *
 *
 * @author Alexey Romanchuk
 * @created 03 нояб. 2014 г.
 *
 */
@ActiveProfiles({ "xxx", "tests" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BaseSpringParentTest.class)
public class BaseSpringParentTest extends BaseSpringParent {

    /**
     * Define a test bean
     * 
     * @return Bean instance
     */
    @Bean(name = "bean")
    public String factory() {

        return "Factory";
    }

    /**
     * Test method for
     * {@link ru.anr.base.tests.BaseTestCase#bean(java.lang.String, java.lang.Class)}
     * .
     */
    @Test
    public void testBean() {

        String x = bean("bean", String.class);
        Assert.assertEquals("Factory", x);
    }

    /**
     * Testing for profiles
     */
    @Test
    public void testProfiles() {

        Set<String> profs = getProfiles();
        Assert.assertEquals(2, profs.size());
        Assert.assertTrue(profs.contains("xxx"));
        Assert.assertTrue(profs.contains("tests"));

        Environment e = getEnv();
        Assert.assertNotNull(e);

        Assert.assertEquals(set(e.getActiveProfiles()), profs);
    }
}
