/**
 * 
 */
package ru.anr.base;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testing for {@link org.springframework.core.env.Environment} access to get
 * profile info in runtime.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 *
 */
@ActiveProfiles("production")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProductionModeTest.class })
public class ProductionModeTest extends BaseSpringParent {

    /**
     * Configuring a bean
     * 
     * @return Bean instance
     */
    @Bean(name = "beanProd")
    public BaseSpringParent instance() {

        return new BaseSpringParent();
    }

    /**
     * Test method for the #isProdMode() method.
     */
    @Test
    public void testIsProdMode() {

        BaseSpringParent s1 = bean("beanProd", BaseSpringParent.class);
        Assert.assertTrue(s1.isProdMode());
    }
}
