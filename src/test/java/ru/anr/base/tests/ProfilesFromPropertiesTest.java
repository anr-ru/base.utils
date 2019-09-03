package ru.anr.base.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.anr.base.BaseSpringParent;

import java.util.Set;

/**
 * Tests for profiles.
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 */

@Configuration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProfilesFromPropertiesTest.class)
@PropertySource("classpath:application.properties")
public class ProfilesFromPropertiesTest extends BaseSpringParent {

    /**
     * Testing for profiles
     */
    @Test
    public void testProfiles() {

        Set<String> profs = getProfiles();
        Assertions.assertEquals(1, profs.size());
        Assertions.assertTrue(profs.contains("production"));

        Environment e = getEnv();
        Assertions.assertNotNull(e);

        Assertions.assertEquals(set(e.getActiveProfiles()), profs);
    }
}
