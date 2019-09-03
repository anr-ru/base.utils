package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Time functions tests.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */

class BaseParentTimeTest extends BaseParent {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseParentTimeTest.class);

    /**
     * Testing for now. Now() provides the time in UTC time zone.
     */
    @Test
    void testDefaultTimeZone() {

        ZonedDateTime t = now();

        ZonedDateTime ekb = ZonedDateTime.now(ZoneId.of("Asia/Yekaterinburg"));

        logger.info("{} - {}", t.toString(), ekb.toString());

        Assertions.assertNotEquals(t, ekb);
        Assertions.assertEquals(60 * 60 * 5, ekb.getOffset().getTotalSeconds() - t.getOffset().getTotalSeconds());
    }

    /**
     * Working with transforming from old dates to zoned ones
     */
    @Test
    void conversionFromADateObject() {

        ZonedDateTime now = now();

        Date t = new Date(now.toInstant().toEpochMilli());
        ZonedDateTime z = date(t);

        Assertions.assertEquals(now, z);

        ZonedDateTime zx = ZonedDateTime.ofInstant(Instant.ofEpochMilli(t.getTime()), ZoneId.of("Asia/Yekaterinburg"));
        Assertions.assertNotEquals(now, zx);
    }

    /**
     * Use case: conversions between zoned dates and basic ones
     */
    @Test
    void testDate() {

        ZonedDateTime z = now();
        Date date = date(z);

        sleep(200);
        Assertions.assertTrue(date.before(date(now())));
        Assertions.assertTrue(z.isBefore(now()));

        ZonedDateTime zx = date(date);
        Assertions.assertEquals(z, zx);
    }

    /**
     * Use case: conversions between zoned dates and calendars
     */
    @Test
    void testCalendar() {

        ZonedDateTime z = now();
        ZonedDateTime zx = date(GregorianCalendar.from(z));

        Assertions.assertEquals(z, zx);

        sleep(200);

        Assertions.assertTrue(z.isBefore(now()));
    }

    /**
     * Use case: understanding zoned time conversions
     */
    @Test
    void testZoneConversions() {

        ZonedDateTime z1 = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Yekaterinburg"));
        ZonedDateTime z2 = ZonedDateTime.ofInstant(Instant.now().minusSeconds(2), ZoneId.of("Asia/Yekaterinburg"));
        Calendar c1 = calendar(z1);
        Calendar c2 = calendar(z2);

        ZonedDateTime zx1 = date(c1); // to UTC
        ZonedDateTime zx2 = date(c2);
        Assertions.assertNotEquals(z1, zx1); // zones distinguish
        Assertions.assertNotEquals(z2, zx2); // zones distinguish

        Assertions.assertTrue(zx2.isBefore(zx1));
        Assertions.assertTrue(z2.isBefore(z1));

        sleep(200);

        Assertions.assertTrue(inPast(zx2));
        Assertions.assertTrue(inPast(zx1));
    }
}
