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

        int nanos = (now.getNano() / 1000000) * 1000000;
        Assertions.assertEquals(now.withNano(nanos), date(t));

        ZonedDateTime zx = ZonedDateTime.ofInstant(Instant.ofEpochMilli(t.getTime()), ZoneId.of("Asia/Yekaterinburg"));
        Assertions.assertNotEquals(now.withNano(nanos), zx);
        Assertions.assertEquals(now.withNano(nanos).toInstant(), zx.toInstant());

        // Equals by the instant value but having different zone IDs
        Assertions.assertNotEquals(now.withNano(nanos), date(GregorianCalendar.from(now)));
        Assertions.assertEquals(now.withNano(nanos).toInstant(), date(GregorianCalendar.from(now)).toInstant());

        Assertions.assertNotEquals(now.withNano(nanos), date(calendar(now)));
        Assertions.assertEquals(now.withNano(nanos).toInstant(), date(calendar(now)).toInstant());

        // By if the ZoneID is not UTC, everything is fine
        Assertions.assertEquals(zx.withNano(nanos), date(calendar(zx)));
        Assertions.assertEquals(zx.withNano(nanos).toInstant(), date(calendar(zx)).toInstant());
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

        int nanos = (z.getNano() / 1000000) * 1000000;
        Assertions.assertEquals(z.withNano(nanos), date(date));
    }

    /**
     * Use case: conversions between zoned dates and calendars
     */
    @Test
    void testCalendar() {

        ZonedDateTime z = now();

        /*
         *  Two observations:
         *  1. Calendars have 'milliseconds' not 'nano seconds'.
         *  2. The zone ID are different for UTC for some reason ('Z' and 'UTC'). This leads to the inequity of time.
         */
        int nanos = (z.getNano() / 1000000) * 1000000;
        Assertions.assertNotEquals(z.withNano(nanos), date(GregorianCalendar.from(z)));
        Assertions.assertEquals(z.withNano(nanos).toInstant(), date(GregorianCalendar.from(z)).toInstant());

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
