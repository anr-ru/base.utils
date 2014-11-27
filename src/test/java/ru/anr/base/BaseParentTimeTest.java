/**
 * 
 */
package ru.anr.base;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Time functions tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */

public class BaseParentTimeTest extends BaseParent {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseParentTimeTest.class);

    /**
     * Testing for now. Now() provides the time in UTC time zone.
     */
    @Test
    public void testDefaultTimeZone() {

        ZonedDateTime t = now();

        ZonedDateTime ekb = ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg"));

        logger.info("{} - {}", t.toString(), ekb.toString());

        Assert.assertNotEquals(t, ekb);
        // After Oct, 26 the difference is 5 hours
        Assert.assertEquals(60 * 60 * 5, ekb.getOffset().getTotalSeconds() - t.getOffset().getTotalSeconds());
    }
}
