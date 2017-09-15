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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * A base class which contains a set of useful functions and short-cuts.
 *
 * The main idea is to reduce the number of code lines in child projects.
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 *
 */

public class BaseParent {

    /**
     * A great function to make Checkstyle think the class has not only static
     * methods.
     */
    public void dummy() {

        // Do nothing
    }

    /**
     * The default encoding
     */
    public static final Charset DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8;

    /**
     * A short-cut for the new list function
     *
     * @param array
     *            An array of objects
     * @return A new list with elements from the original array
     *
     * @param <S>
     *            The type of elements
     */
    @SafeVarargs
    public static <S> List<S> list(S... array) {

        return new ArrayList<S>(Arrays.asList(array));
    }

    /**
     * Converts a string in the given encoding to a byte array without throwing
     * a checked exception.
     * 
     * 
     * @param s
     *            A string
     * @param encoding
     *            An encoding
     * @return An array of bytes
     */
    public static byte[] bytes(String s, String encoding) {

        try {
            return s.getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Converts a string to its utf-8 bytes
     * 
     * @param s
     *            An original string
     * @return An array of bytes
     */
    public static byte[] utf8(String s) {

        return bytes(s, DEFAULT_CHARSET.name());
    }

    /**
     * Converts bytes to a string with the utf8 encoding
     * 
     * @param b
     *            An array of bytes
     * @return A string
     */
    public static String utf8(byte[] b) {

        try {
            return new String(b, DEFAULT_CHARSET.name());
        } catch (UnsupportedEncodingException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Null-safe creation of a list
     *
     * @param l
     *            An original, possible, null list
     * @return A new list with elements from the original one
     *
     * @param <S>
     *            The type of elements
     */
    public static <S> List<S> list(List<S> l) {

        return l == null ? new ArrayList<S>() : new ArrayList<S>(l);
    }

    /**
     * A short-cut for creation of a set from an array.
     *
     * @param array
     *            An array of objects
     * @return A new set with elements from the original array
     *
     * @param <S>
     *            The type of elements
     */
    @SafeVarargs
    public static <S> Set<S> set(S... array) {

        return new HashSet<S>(Arrays.asList(array));
    }

    /**
     * Concatenation of two arrays into one
     *
     * @param array1
     *            The first array
     * @param array2
     *            The second
     * @return The resulted array
     *
     * @param <S>
     *            The type of array elements
     */
    @SafeVarargs
    public static <S> S[] concat(S[] array1, S... array2) {

        return ArrayUtils.addAll(array1, array2);
    }

    /**
     * Injection of a private field into the given object. Of course, we are
     * against such a technique :-). But still in tests this is a very
     * convenient approach.
     *
     * @param target
     *            The target object
     * @param fieldName
     *            The name of the field to inject
     * @param field
     *            The field's value
     */
    public static void inject(Object target, String fieldName, Object field) {

        Field f = ReflectionUtils.findField(target.getClass(), fieldName);
        Assert.notNull(f);

        ReflectionUtils.makeAccessible(f);
        ReflectionUtils.setField(f, target, field);
    }

    /**
     * Returns the result of the equals operation even if the arguments are
     * null.
     * 
     * @param arg1
     *            A first argument
     * @param arg2
     *            A second argument
     * @return The result of the equals operation or false if one or both
     *         arguments are null
     */
    public static boolean safeEquals(Object arg1, Object arg2) {

        return ObjectUtils.nullSafeEquals(arg1, arg2);
    }

    /**
     * Retrieves the first object of a collection.
     * 
     * @param coll
     *            An original collection
     * @return The found object or null is the collection is empty
     * 
     * @param <S>
     *            The type of elements
     */
    public static <S> S first(Collection<S> coll) {

        return CollectionUtils.isEmpty(coll) ? null : coll.iterator().next();
    }

    /**
     * A short-cut method for finding the first item in the given stream.
     * 
     * @param stream
     *            A stream
     * @return The resulted value if it exists, otherwise null
     * 
     * @param <S>
     *            The type of stream's elements
     */
    public static <S> S first(Stream<S> stream) {

        return (stream == null) ? null : stream.findFirst().orElse(null);
    }

    /**
     * Filters the given collection according to the specified predicate which
     * can be a lambda expression.
     * 
     * @param coll
     *            An original collection
     * @param predicate
     *            A predicate (can be a lambda expression)
     * @return The filtered collection
     * 
     * @param <S>
     *            The type of collection's items
     */
    public static <S> List<S> filter(Collection<S> coll, Predicate<S> predicate) {

        return coll.stream().filter(predicate).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sleeps ignoring {@link InterruptedException}.
     * 
     * @param millis
     *            A number of milliseconds
     */
    public static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * A short-cut method to do the toString() operation with checking the
     * argument to be a possible null value value.
     * 
     * @param s
     *            An original object (can be null)
     * @return A string which is not null
     */
    public static String nullSafe(Object s) {

        return (s == null) ? "" : s.toString();
    }

    /**
     * Converts an array of pairs of elements to a Map&lt;K,S&gt; object with
     * casting to K and S types.
     * 
     * @param array
     *            Pairs (key, value) given as a plain array
     * @return A new map
     * 
     * @param <K>
     *            The type of keys
     * @param <T>
     *            The type of values
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
     * A short-cut method for simple mapping from a collection to a map
     * 
     * @param collection
     *            A collection
     * @param keyMapper
     *            A key mapper
     * @param valueMapper
     *            A value mapper
     * @return A new created map
     * 
     * @param <T>
     *            The type of collection items
     * @param <K>
     *            The type of map keys
     * @param <U>
     *            The type of map values
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {

        return collection.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * A short-cut function to simplify the process of building a map from a
     * collection when there can be non-unique keys.
     * 
     * @param collection
     *            An original collection
     * @param keyMapper
     *            A key mapper
     * @param valueMapper
     *            A value mapper
     * @param mergeFunction
     *            A function to merge values conflicting by their key
     * @return A new map
     * 
     * @param <T>
     *            The type of a collection item
     * @param <K>
     *            Type of the map key
     * @param <U>
     *            Type of the map value
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {

        return collection.stream().collect(Collectors.toConcurrentMap(keyMapper, valueMapper, mergeFunction));
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
     * Determines whether the given collection contains specified strings or
     * not. The parameter conjunction must be 'true' if we expect all strings to
     * be included in the source collection, otherwise it is 'false' (That means
     * at least one must be included).
     * 
     * @param coll
     *            A source collection
     * @param conjunction
     *            true, if all inclusion are expected, or false, if at least
     *            one.
     * @param items
     *            Expected strings
     * @return true, if the given collection contains the specified items
     *         according to the condition 'conjunction'.
     */
    protected static boolean contains(Collection<String> coll, boolean conjunction, String... items) {

        Set<String> s = set(items);
        return conjunction ? //
                coll.containsAll(list(items)) : //
                coll.stream().parallel().filter(a -> s.contains(a)).count() > 0;
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
     * Get first key without value
     * 
     * @param <T>
     *            type of keys
     * @param map
     *            map
     * @param keys
     *            keys
     * @return first key without value or <code>null</code>
     */
    public static <T> List<T> getEmptyKeys(Map<T, ?> map, List<T> keys) {

        return list(keys.stream().filter(key -> map.get(key) == null));
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
         * @throws Exception
         *             An exception if occurs
         */
        void run(Object... params) throws Exception;
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
        } catch (Exception ignore) {
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
     * Transforms the given calendar value to a zoned date from the default
     * (UTC) time zone.
     * 
     * @param calendar
     *            Some calendar value
     * @return A zoned date-time
     */
    public static ZonedDateTime date(Calendar calendar) {

        return ZonedDateTime.ofInstant(calendar.toInstant(), DEFAULT_TIMEZONE);
    }

    /**
     * Transforms the given zoned date-time object to a gregorian calendar
     * 
     * @param dateTime
     *            A date-time object
     * @return A calendar object
     */
    public static Calendar calendar(ZonedDateTime dateTime) {

        return GregorianCalendar.from(dateTime);
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
     * Checks the given date-time object was in the past
     * 
     * @param z
     *            Some date-time object
     * @return true, if the given date was in the past comparing with the
     *         present time
     */
    public static boolean inPast(ZonedDateTime z) {

        return z.isBefore(now());
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

    /**
     * Reading a file from the class path
     * 
     * @param path
     *            A path to the file's location
     * @return The content as a string
     */
    public static String readAsString(String path) {

        try {
            return IOUtils.toString(new ClassPathResource(path).getInputStream());
        } catch (IOException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Date formatting
     * 
     * @param date
     *            Date
     * @param locale
     *            Locale
     * @return formatted date
     */
    public static String formatDate(long date, String locale) {

        return DateTimeFormatter.ofPattern(locale.equals("ru_RU") ? "dd.MM.yyyy HH:mm:ss z" : "dd/MM/yyyy HH:mm:ss z")
                .withZone(ZoneOffset.systemDefault())
                .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()));
    }
}
