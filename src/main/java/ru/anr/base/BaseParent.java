/*
 * Copyright 2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.anr.base;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Base class, which contains a set of useful functions and short-cut to the
 * functions.
 *
 * The main idea is to reduce a number of code lines in child projects.
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 *
 */

public class BaseParent {

    /**
     * The great function to make Checkstyle think the class doesn't have static
     * methods only
     */
    public void dummy() {

        // Do nothing
    }

    /**
     * The default encoding
     */
    public static final Charset DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8;

    /**
     * Short-cut for new List function
     *
     * @param array
     *            Array of objects
     * @return New list with elements from original array
     *
     * @param <S>
     *            Type of elements
     */
    @SafeVarargs
    public static <S> List<S> list(S... array) {

        return new ArrayList<S>(Arrays.asList(array));
    }

    /**
     * No-expection converting a string to byte array
     * 
     * @param s
     *            String
     * @param encoding
     *            Encoding
     * @return Array of bytes
     */
    public static byte[] bytes(String s, String encoding) {

        try {
            return s.getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Convering s to utf8 bytes
     * 
     * @param s
     *            Original string
     * @return Array of bytes
     */
    public static byte[] utf8(String s) {

        return bytes(s, "utf8");
    }

    /**
     * Convering bytes to Sring with utf8 encoding
     * 
     * @param b
     *            Array of bytes
     * @return A String
     */
    public static String utf8(byte[] b) {

        try {
            return new String(b, "utf8");
        } catch (UnsupportedEncodingException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Null-safe creation of list
     *
     * @param l
     *            Original (possible null) list
     * @return New list with elements from original one
     *
     * @param <S>
     *            Type of elements
     */
    public static <S> List<S> list(List<S> l) {

        return l == null ? new ArrayList<S>() : new ArrayList<S>(l);
    }

    /**
     * Short-cut for new List function
     *
     * @param array
     *            Array of objects
     * @return New list with elements from original array
     *
     * @param <S>
     *            Type of elements
     */
    @SafeVarargs
    public static <S> Set<S> set(S... array) {

        return new HashSet<S>(Arrays.asList(array));
    }

    /**
     * Concatination of two arrays
     *
     * @param array1
     *            The first array
     * @param array2
     *            The second
     * @return Resulted array
     *
     * @param <S>
     *            Type of array elements
     */
    @SafeVarargs
    public static <S> S[] concat(S[] array1, S... array2) {

        return ArrayUtils.addAll(array1, array2);
    }

    /**
     * Injection of private field into object
     *
     * @param target
     *            Object
     * @param fieldName
     *            Name of the field
     * @param field
     *            The field value
     */
    public static void inject(Object target, String fieldName, Object field) {

        Field f = ReflectionUtils.findField(target.getClass(), fieldName);
        Assert.notNull(f);

        ReflectionUtils.makeAccessible(f);
        ReflectionUtils.setField(f, target, field);
    }

    /**
     * Returns a result of equals operation event if arguments are null.
     * 
     * @param arg1
     *            A first argument
     * @param arg2
     *            A second argument
     * @return Result of equals operation or false, if one or both arguments are
     *         null
     */
    public static boolean safeEquals(Object arg1, Object arg2) {

        return ObjectUtils.nullSafeEquals(arg1, arg2);
    }

    /**
     * Retrieving a first object of collection (depending on its iterating way)
     * 
     * @param coll
     *            Original collection
     * @return Found object or null
     * 
     * @param <S>
     *            Expected object class
     */
    public static <S> S first(Collection<S> coll) {

        return CollectionUtils.isEmpty(coll) ? null : coll.iterator().next();
    }

    /**
     * A short-cut method for finding a first item in the given stream
     * 
     * @param stream
     *            A stream
     * @return A resulted value if exist, otherwise null
     * 
     * @param <S>
     *            The type of the resulted value
     */
    public static <S> S first(Stream<S> stream) {

        return (stream == null) ? null : stream.findFirst().orElse(null);
    }

    /**
     * Filters a specified collection according to predicate, which can be a
     * lambda expression.
     * 
     * @param coll
     *            Original collection
     * @param predicate
     *            Predicated (can be a lambda expression)
     * @return Filtered collection
     * 
     * @param <S>
     *            Class of collection items
     */
    public static <S> List<S> filter(Collection<S> coll, Predicate<S> predicate) {

        return coll.stream().filter(predicate).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sleeping without {@link InterruptedException} try/catch necessary.
     * 
     * @param millis
     *            Number of milliseconds
     */
    public static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * Short-cut to return String, which givers an empty string in case of null
     * value.
     * 
     * @param s
     *            Original string (or even object)
     * @return Empty string is null
     */
    public static String nullSafe(Object s) {

        return (s == null) ? "" : s.toString();
    }

    /**
     * Converts array of any elements to Map&lt;K,S&gt;
     * 
     * with specified casting to K and S types.
     * 
     * @param array
     *            An array of some elements
     * @return {@link Map}
     * 
     * @param <K>
     *            Type of keys
     * @param <T>
     *            Type of array values
     * @param <S>
     *            Type of map values
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <K, S, T> Map<K, S> toMap(T... array) {

        Map<K, S> map = new HashMap<>();
        if (array != null && array.length > 0) {

            int l = (array.length % 2 == 0) ? array.length : (array.length + 1);

            for (int i = 0; i < (l / 2); i = i + 1) {

                Object v = (2 * i + 1) >= array.length ? null : array[2 * i + 1];
                map.put((K) array[2 * i], (S) v);
            }
        }
        return map;
    }

    /**
     * Short-cut for simple mapping from a collection to a map
     * 
     * @param collection
     *            The collection
     * @param keyMapper
     *            A key mapper
     * @param valueMapper
     *            A value mapper
     * @return A new created map
     * 
     * @param <T>
     *            Type of a collection item
     * @param <K>
     *            Type of the map key
     * @param <U>
     *            Type of the map value
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {

        return collection.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * Safe parsing of string to some {@link Number}
     * 
     * @param x
     *            String value
     * @param clazz
     *            Class of Number object
     * @return Parsed value or null if parsing was unsuccessful
     * 
     * @param <S>
     *            Object class
     */
    public static <S extends Number> S parse(String x, Class<S> clazz) {

        S v = null;
        try {
            v = inst(clazz, new Class<?>[]{ String.class }, x);
        } catch (FunctorException | IllegalArgumentException ex) {
            v = null;
        }
        return v;
    }

    /**
     * Instantiation of object
     * 
     * @param clazz
     *            Class to instantiate
     * @param paramTypes
     *            Constructor arguments
     * @param args
     *            Any arguments provided
     * @return An object
     * 
     * @param <S>
     *            Object class
     */
    public static <S> S inst(Class<? extends S> clazz, Class<?>[] paramTypes, Object... args) {

        return FactoryUtils.instantiateFactory(clazz, paramTypes, args).create();
    }

    /**
     * Generates 'GUID'
     * 
     * @return String with GUID
     */
    public static String guid() {

        return UUID.randomUUID().toString();
    }

    /**
     * Short-cut for creation of {@link BigDecimal}.
     * 
     * @param numberAsStr
     *            String representation for decimal
     * @return a new {@link BigDecimal}
     */
    public static final BigDecimal d(String numberAsStr) {

        return new BigDecimal(numberAsStr);
    }

    /**
     * Short-cut for creation of {@link BigDecimal} from double value
     * 
     * @param value
     *            Double value
     * @return a new {@link BigDecimal}
     */
    public static final BigDecimal d(double value) {

        return new BigDecimal(Double.toString(value));
    }

    /**
     * Short-cut for set a scale for {@link BigDecimal} using the standard
     * {@link RoundingMode#HALF_UP} mode (so called 'school' rounding).
     * 
     * @param d
     *            A decimal value
     * @param scale
     *            The scale
     * @return New {@link BigDecimal} in the specified scale
     */
    public static final BigDecimal scale(BigDecimal d, int scale) {

        return d.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * Short-cut for division with specific
     * 
     * @param a
     *            A divided value
     * @param b
     *            A divisor
     * 
     * @param scale
     *            The scale
     * @return New {@link BigDecimal} in the specified scale
     */
    public static final BigDecimal div(BigDecimal a, BigDecimal b, int scale) {

        return a.divide(b, scale, RoundingMode.HALF_UP);
    }

    /**
     * Clone the specified object, if allowed
     * 
     * @param o
     *            Clonable
     * @return Clone (or null, if the original object was null)
     * 
     * @param <S>
     *            Object type
     */
    @SuppressWarnings("unchecked")
    public static <S> S cloneObject(Object o) {

        return (S) org.apache.commons.lang3.ObjectUtils.cloneIfPossible(o);
    }

    /**
     * A short-cut for the most often variant of the function
     * 
     * @param c
     *            A collection
     * @return true, if it's empty
     */
    public static boolean notEmpty(Collection<?> c) {

        return !CollectionUtils.isEmpty(c);
    }

    /**
     * A short-cut for the often used variant
     * 
     * @param s
     *            A string
     * @return true, if the string is NOT empty
     */
    public static boolean notEmpty(Object s) {

        return !isEmpty(s);
    }

    /**
     * A short-cut for the often used variant
     * 
     * @param s
     *            A string
     * @return true, if the string is empty
     */
    public static boolean isEmpty(Object s) {

        return StringUtils.isEmpty(s);
    }

    /**
     * A simpler form for stream collecting
     * 
     * @param stream
     *            A stream
     * @return A list
     * @param <S>
     *            Type of object
     */
    public static <S> List<S> list(Stream<S> stream) {

        return stream.collect(Collectors.toList());
    }

    /**
     * Calculates a total value for the specified stream. The given mapper
     * function provides a conversion operation from an object of the type S to
     * a {@link BigDecimal} value.
     * 
     * @param stream
     *            The stream to use
     * @param mapper
     *            The mapper function
     * @return The calculated result of decimal value
     * @param <S>
     *            The supposed type of objects in the stream
     */
    public static <S> BigDecimal total(Stream<S> stream, Function<? super S, BigDecimal> mapper) {

        return stream.map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates a total value for the specified stream. The given mapper
     * function provides a conversion operation from an object of the type S to
     * a {@link BigDecimal} value.
     * 
     * @param list
     *            The list of objects
     * @param mapper
     *            The mapper function
     * @return The calculated result of decimal value
     * @param <S>
     *            The supposed type of objects in the stream
     */
    public static <S> BigDecimal total(List<S> list, Function<? super S, BigDecimal> mapper) {

        return total(list.stream(), mapper);
    }

    /**
     * A short-cut to Map procedure
     * 
     * @param stream
     *            A stream
     * @param keyMapper
     *            The key mapper
     * @param valueMapper
     *            The value mapper
     * @return A map
     * @param <S>
     *            Type of object in the original stream
     * @param <K>
     *            The key type
     * @param <U>
     *            The value type
     */
    public static <S, K, U> Map<K, U> map(Stream<S> stream, Function<? super S, ? extends K> keyMapper,
            Function<? super S, ? extends U> valueMapper) {

        return stream.collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * A simpler form for stream collecting
     * 
     * @param stream
     *            A stream
     * @return A set
     * @param <S>
     *            Type of object
     */
    public static <S> Set<S> set(Stream<S> stream) {

        return stream.collect(Collectors.toSet());
    }

    /**
     * The 'RunIgnoreErrors' interface
     */
    @FunctionalInterface
    public interface RunIgnoreErrors {

        /**
         * @param params
         *            Parameters
         */
        void run(Object... params);
    }

    /**
     * Executes something specified in the callback but ignores any exceptions
     * and writes them to the error log
     * 
     * @param callback
     *            The callback to use
     * @param params
     *            A set of parameters
     */
    protected void runIgnored(RunIgnoreErrors callback, Object... params) {

        try {
            callback.run(params);
        } catch (AssertionError ignore) {
            error("Ignored error: {}", nullSafe(ignore.getMessage()));

        } catch (RuntimeException ignore) {
            error("Ignored error: {}", nullSafe(ignore.getMessage()));
        }
    }

    // //////////////////////////// TIME FUNCTIONS ///////////////////////////

    /**
     * A reference to default timezone
     */
    public static final ZoneId DEFAULT_TIMEZONE = ZoneOffset.UTC;

    /**
     * A clock if we need to use different clocks (in tests, for example)
     */
    private static Clock clock;

    /**
     * Sets or changes the current clock
     * 
     * @param c
     *            A clock
     */
    public static void setClock(Clock c) {

        clock = c;
    }

    /**
     * Current time. It always is represented in UTC.
     * 
     * @return Time represented as {@link ZonedDateTime} object
     */
    public static ZonedDateTime now() {

        return (clock == null) ? ZonedDateTime.now(DEFAULT_TIMEZONE) : ZonedDateTime.now(clock);
    }

    /**
     * We have to use old Date object, because Hibernate/JPA does not support
     * Java 8 dates (see https://java.net/jira/browse/JPA_SPEC-63 or
     * https://hibernate.atlassian.net/browse/HHH-8844).
     * 
     * @param dateTime
     *            Date time in Java 8 format
     * @return Old Date object
     */
    public static Date date(ZonedDateTime dateTime) {

        return Date.from(dateTime.toInstant());
    }

    /**
     * Transforms an old date object to a zoned date using the default (UTC)
     * time zone
     * 
     * @param oldDate
     *            Some old date
     * @return A zoned object
     */
    public static ZonedDateTime date(Date oldDate) {

        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(oldDate.getTime()), DEFAULT_TIMEZONE);
    }

    /**
     * Instant logging (usually for testing)
     * 
     * @param msg
     *            A log message
     * @param arguments
     *            A set of argument of the message
     */
    public static void log(String msg, Object... arguments) {

        LoggerFactory.getLogger(BaseParent.class).info(msg, arguments);
    }

    /**
     * Instant logging for an error (usually for testing)
     * 
     * @param msg
     *            A log message
     * @param arguments
     *            A set of argument of the message
     */
    public static void error(String msg, Object... arguments) {

        LoggerFactory.getLogger(BaseParent.class).error(msg, arguments);
    }

    /**
     * A callback used for waitCondition(...)
     */
    @FunctionalInterface
    public interface SleepCallback {

        /**
         * Some action which should return true or false
         * 
         * @param args
         *            Some arguments
         * @return if true that means the cycle must be stopped
         */
        boolean doAction(Object... args);
    }

    /**
     * The progress bar of expectations
     */
    private static final Set<Integer> PERCENTS = set(10, 25, 50, 75, 90);

    /**
     * Performs an expectation cycle during the specified number of seconds and
     * checks the condition on each iteration.
     * 
     * @param secs
     *            The number of seconds
     * @param sleepTime
     *            Sleep time in milliseconds
     * @param logProgress
     *            true, if it is required to log the progress
     * @param callback
     *            The callback
     * @param args
     *            The arguments
     * @return true, if the the number of attempts has been exceeded
     */
    public static boolean waitCondition(int secs, int sleepTime, boolean logProgress, SleepCallback callback,
            Object... args) {

        int counter = 0;
        Set<Integer> s = new HashSet<>(PERCENTS);

        while (!callback.doAction(args)) {

            int tick = (100 * counter / (secs * 1000));
            List<Integer> r = filter(s, i -> i < tick);

            if (!r.isEmpty()) {
                if (logProgress) {
                    log("Wait Progress: {} %", r.get(0));
                }
                s.removeAll(r);
            }
            counter += sleepTime;

            if (counter > (secs * 1000)) {
                break;
            }
            sleep(sleepTime);
        }
        return counter > (secs * 1000);
    }

    /**
     * Performs an expectation cycle during the specified number of seconds and
     * checks the condition on each iteration.
     * 
     * @param secs
     *            The number of seconds
     * @param logProgress
     *            true, if it is required to log the progress
     * @param callback
     *            The callback
     * @param args
     *            The arguments
     * @return true, if the the number of attempts has been exceeded
     */
    public static boolean waitCondition(int secs, boolean logProgress, SleepCallback callback, Object... args) {

        return waitCondition(secs, 500, logProgress, callback, args);
    }

}
