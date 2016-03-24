/**
 * 
 */
package ru.anr.base.tests;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.anr.base.BaseSpringParent;

/**
 * Tests for profiles.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 *
 */

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProfilesFromPropertiesTest.class)
@PropertySource("classpath:application.properties")
public class ProfilesFromPropertiesTest extends BaseSpringParent {

    /**
     * Testing for profiles
     */
    @Test
    public void testProfiles() {

        Set<String> profs = getProfiles();
        Assert.assertEquals(1, profs.size());
        Assert.assertTrue(profs.contains("production"));

        Environment e = getEnv();
        Assert.assertNotNull(e);

        Assert.assertEquals(set(e.getActiveProfiles()), profs);
    }
}
