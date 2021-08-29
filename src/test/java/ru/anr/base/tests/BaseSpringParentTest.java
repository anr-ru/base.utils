package ru.anr.base.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.anr.base.BaseSpringParent;

import java.util.Set;

/**
 * Tests for {@link BaseSpringParent}.
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 */
@ActiveProfiles({"xxx", "tests"})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BaseSpringParentTest.class)
class BaseSpringParentTest extends BaseSpringParent {

    /**
     * Define a test bean
     *
     * @return Bean instance
     */
    @Bean(name = "bean")
    String factory() {
        return "Factory";
    }

    /**
     * Test method for bean(...).
     */
    @Test
    void testBean() {

        String x = bean("bean", String.class);
        Assertions.assertEquals("Factory", x);

        x = bean("bean");
        Assertions.assertEquals("Factory", x);

        x = bean(String.class);
        Assertions.assertEquals("Factory", x);
        Assertions.assertTrue(hasBean("bean"));
    }

    @Test
    void testBeanTarget() {

        String x = target(bean("bean", String.class));
        Assertions.assertEquals("Factory", x);
    }


    /**
     * Testing for profiles
     */
    @Test
    void testProfiles() {

        Set<String> profs = getProfiles();
        Assertions.assertEquals(2, profs.size());
        Assertions.assertTrue(profs.contains("xxx"));
        Assertions.assertTrue(profs.contains("tests"));

        Environment e = getEnv();
        Assertions.assertNotNull(e);

        Assertions.assertEquals(set(e.getActiveProfiles()), profs);
    }
}
