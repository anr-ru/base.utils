/**
 * 
 */
package ru.anr.base;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

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
        Assert.assertEquals(60 * 60 * 5, ekb.getOffset().getTotalSeconds() - t.getOffset().getTotalSeconds());
    }

    /**
     * Working with transforming from old dates to zoned ones
     */
    @Test
    public void conversionFromADateObject() {

        ZonedDateTime now = now();

        Date t = new Date(now.toInstant().toEpochMilli());
        ZonedDateTime z = date(t);

        Assert.assertEquals(now, z);

        ZonedDateTime zx = ZonedDateTime.ofInstant(Instant.ofEpochMilli(t.getTime()), ZoneId.of("Asia/Yekaterinburg"));
        Assert.assertNotEquals(now, zx);
    }
}
