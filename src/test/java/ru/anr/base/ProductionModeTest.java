package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Testing for {@link org.springframework.core.env.Environment} access to get
 * profile info in runtime.
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 */
@ActiveProfiles("production")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProductionModeTest.class})
class ProductionModeTest extends BaseSpringParent {

    /**
     * Configuring a bean
     *
     * @return Bean instance
     */
    @Bean(name = "beanProd")
    BaseSpringParent instance() {

        return new BaseSpringParent();
    }

    /**
     * Test method for the #isProdMode() method.
     */
    @Test
    void testIsProdMode() {

        BaseSpringParent s1 = bean("beanProd", BaseSpringParent.class);
        Assertions.assertTrue(s1.isProdMode());
    }
}
