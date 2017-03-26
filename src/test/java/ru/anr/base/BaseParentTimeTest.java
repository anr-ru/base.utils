/**
 * 
 */
package ru.anr.base;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    /**
     * Use case: conversions between zoned dates and basic ones
     */
    @Test
    public void testDate() {

        ZonedDateTime z = now();
        Date date = date(z);

        sleep(200);
        Assert.assertTrue(date.before(date(now())));
        Assert.assertTrue(z.isBefore(now()));

        ZonedDateTime zx = date(date);
        Assert.assertEquals(z, zx);
    }

    /**
     * Use case: conversions between zoned dates and calendars
     */
    @Test
    public void testCalendar() {

        ZonedDateTime z = now();
        ZonedDateTime zx = date(GregorianCalendar.from(z));

        Assert.assertEquals(z, zx);

        sleep(200);

        Assert.assertTrue(z.isBefore(now()));
    }

    /**
     * Use case: understanding zoned time conversions
     */
    @Test
    public void testZoneConversions() {

        ZonedDateTime z1 = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Yekaterinburg"));
        ZonedDateTime z2 = ZonedDateTime.ofInstant(Instant.now().minusSeconds(2), ZoneId.of("Asia/Yekaterinburg"));
        Calendar c1 = calendar(z1);
        Calendar c2 = calendar(z2);

        ZonedDateTime zx1 = date(c1); // to UTC
        ZonedDateTime zx2 = date(c2);
        Assert.assertNotEquals(z1, zx1); // zones distinguish
        Assert.assertNotEquals(z2, zx2); // zones distinguish

        Assert.assertTrue(zx2.isBefore(zx1));
        Assert.assertTrue(z2.isBefore(z1));

        sleep(200);

        Assert.assertTrue(inPast(zx2));
        Assert.assertTrue(inPast(zx1));
    }
}
