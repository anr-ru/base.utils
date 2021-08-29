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

import org.apache.commons.codec.binary.Hex;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A base class which contains a set of useful functions and short-cuts.
 * <p>
 * The main idea is to reduce the number of code lines in child projects.
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 */

public class BaseParent {

    /**
     * The default encoding
     */
    public static final Charset DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8;

    /**
     * A short-cut function for creating a new list from an array
     *
     * @param array The array of objects
     * @param <S>   The type of elements
     * @return The resulted list with elements from the original array
     */
    @SafeVarargs
    public static <S> List<S> list(S... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * A short-cut method for building a list by a collection
     *
     * @param collection The collection to convert
     * @param <S>        The type of items
     * @return The resulted list
     */
    public static <S> List<S> list(Collection<S> collection) {
        return new ArrayList<>(collection);
    }

    /**
     * Converts a string in the given encoding to a byte array without throwing
     * a checked exception.
     *
     * @param s        The original string
     * @param encoding The encoding
     * @return The resulted array of bytes
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
     * @param s The original string
     * @return The resulted array of bytes
     */
    public static byte[] utf8(String s) {
        return bytes(s, DEFAULT_CHARSET.name());
    }

    /**
     * Converts bytes to a string with the utf8 encoding
     *
     * @param b An array of bytes
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
     * @param l   The original, possibly, null list
     * @param <S> The type of elements
     * @return A new list with elements from the original one
     */
    public static <S> List<S> list(List<S> l) {
        return nullSafe(l, ArrayList::new).orElse(new ArrayList<>());
    }

    /**
     * A simpler shot-cut for collecting a stream to a list.
     *
     * @param stream The stream
     * @param <S>    The object type
     * @return The resulted list
     */
    public static <S> List<S> list(Stream<S> stream) {
        return stream.collect(Collectors.toList());
    }

    /**
     * A short-cut for creation of a set from an array.
     *
     * @param array An array of objects
     * @param <S>   The type of elements
     * @return A new set with elements from the original array
     */
    @SafeVarargs
    public static <S> Set<S> set(S... array) {
        return new HashSet<>(Arrays.asList(array));
    }

    /**
     * A simpler form for stream collecting
     *
     * @param stream A stream
     * @param <S>    Type of object
     * @return A set
     */
    public static <S> Set<S> set(Stream<S> stream) {
        return stream.collect(Collectors.toSet());
    }

    /**
     * Concatenation of two arrays into one
     *
     * @param array1 The first array
     * @param array2 The second
     * @param <S>    The type of array elements
     * @return The resulted array
     */
    @SafeVarargs
    public static <S> S[] concat(S[] array1, S... array2) {
        return ArrayUtils.addAll(array1, array2);
    }

    /**
     * Injection of a private field into the given object. Of course, we are
     * against such a technique :-).
     *
     * But still in tests this is a very convenient approach.
     *
     * @param target     The target object
     * @param fieldName  The name of the field to inject
     * @param fieldValue The field's value
     */
    public static void inject(Object target, String fieldName, Object fieldValue) {

        Field f = ReflectionUtils.findField(target.getClass(), fieldName);
        Assert.notNull(f, "Field " + fieldName + " not defined");

        ReflectionUtils.makeAccessible(f);
        ReflectionUtils.setField(f, target, fieldValue);
    }

    /**
     * Extracts the field value from the object using the reflection API.
     *
     * @param target    The target object
     * @param fieldName The name of the field
     * @param <S>       The expected type of resulted object. The type casting is unsafe
     *                  as we suppose the user expects the valid type.
     * @return The resulted object
     */
    @SuppressWarnings("unchecked")
    public static <S> S field(Object target, String fieldName) {

        Field f = ReflectionUtils.findField(target.getClass(), fieldName);
        Assert.notNull(f, "Field " + fieldName + " not defined");

        ReflectionUtils.makeAccessible(f);
        return (S) ReflectionUtils.getField(f, target);
    }

    /**
     * Returns the result of the equals operation even if the arguments are
     * null.
     *
     * @param arg1 A first argument
     * @param arg2 A second argument
     * @return The result of the equals operation or false if one or both
     * arguments are null
     */
    public static boolean safeEquals(Object arg1, Object arg2) {
        return ObjectUtils.nullSafeEquals(arg1, arg2);
    }

    /**
     * Retrieves the first object of a collection.
     *
     * @param coll An original collection
     * @param <S>  The type of elements
     * @return The found object or null is the collection is empty
     */
    public static <S> S first(Collection<S> coll) {
        return CollectionUtils.isEmpty(coll) ? null : coll.iterator().next();
    }

    /**
     * A short-cut method for finding the first item in the given stream.
     *
     * @param stream A stream
     * @param <S>    The type of stream's elements
     * @return The resulted value if it exists, otherwise null
     */
    public static <S> S first(Stream<S> stream) {
        return nullSafe(stream, s -> s.findFirst().orElse(null))
                .orElse(null);
    }

    /**
     * Filters the given collection according to the specified predicate which
     * can be a lambda expression.
     *
     * @param coll      An original collection
     * @param predicate A predicate (can be a lambda expression)
     * @param <S>       The type of collection's items
     * @return The filtered collection
     */
    public static <S> List<S> filter(Collection<S> coll, Predicate<S> predicate) {
        return coll.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Adds an item to the given collection if the item is not null
     *
     * @param coll A collection
     * @param item Some item
     * @param <S>  The type of the item
     * @return true, if the item has been added
     */
    public static <S> Collection<S> safeAdd(Collection<S> coll, S item) {
        if (item != null) {
            coll.add(item);
        }
        return coll;
    }

    /**
     * Adds the given value to the collection if the value is not null
     * and return the updated collection.
     *
     * @param coll The updated collection
     * @param item The item
     * @param <S>  The type of object
     * @return true, if the item was added
     */
    public static <S> boolean add(Collection<S> coll, S item) {
        boolean rs = false;
        if (item != null) {
            coll.add(item);
            rs = true;
        }
        return rs;
    }

    /**
     * Sleeps ignoring {@link InterruptedException}.
     *
     * @param millis A number of milliseconds
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
     * @param s An original object (can be null)
     * @return A string which is not null
     */
    public static String nullSafe(Object s) {
        return (s == null) ? "" : s.toString();
    }

    /**
     * A function that allows to process correctly values that can be null.
     *
     * @param value    The value
     * @param callback The callback
     * @param <S>      The type of the result value
     * @param <V>      The original value type
     * @return The resulted optional
     */
    public static <S, V> Optional<S> nullSafe(V value, Function<V, S> callback) {
        return (value == null) ? Optional.empty() : Optional.ofNullable(callback.apply(value));
    }

    /**
     * A null-safe initialization of object. If the original object is not
     * null, then it is simply returned. But in case of null object, a new
     * one is created.
     * <p>
     * This approach allows to safely get an instance of the object
     * even if it is null.
     * </p>
     *
     * @param o     The original object (possibly, null)
     * @param clazz The class of the object
     * @param <S>   The object type
     * @return The old object or a new created
     */
    public static <S> S nullSafe(S o, Class<S> clazz) {
        return Optional.ofNullable(o).orElse(inst(clazz, new Class<?>[]{}));
    }

    /**
     * Converts an array of pairs of elements to a Map&lt;K,S&gt; object with
     * casting to K and S types.
     *
     * @param array Pairs (key, value) given as a plain array
     * @param <K>   The type of keys
     * @param <T>   The type of values
     * @param <S>   Type of map values
     * @return A new map
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <K, S, T> Map<K, S> toMap(T... array) {

        Map<K, S> map = new LinkedHashMap<>(); // The order is important
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
     * A short-cut method for simple mapping from a collection to a map.
     * The simplest usage of the function is something like this:
     * <pre>{@code}
     *      List&lt;SampleObject&gt; s = list( new Sample("xxx", 1), new Sample("yyy", 2) );
     *      Map&lt;String, Integer&gt; map = toMap(s, SampleObject::getValue, SampleObject::getIndex);
     *
     *      // The resulted map contains { "xxx": 1, "yyy": 2 }
     *
     * </pre>
     *
     * @param collection  A collection
     * @param keyMapper   A key mapper
     * @param valueMapper A value mapper
     * @param <T>         The type of collection items
     * @param <K>         The type of map keys
     * @param <U>         The type of map values
     * @return A new created map
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection,
                                            Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper) {

        return collection.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * A short-cut function to simplify the process of building a map from a
     * collection when there can be non-unique keys.
     *
     * <pre>{@code}
     *      List&lt;SampleObject&gt; s = list( new Sample("xxx", 1), new Sample("xxx", 2) );
     *      Map&lt;String, Integer&gt; map = toMap(s, SampleObject::getValue, SampleObject::getIndex, Integer::sum);
     *
     *      // The resulted map contains { "xxx": 3 }
     *
     * </pre>
     *
     * @param collection    The original collection
     * @param keyMapper     The key mapper
     * @param valueMapper   The value mapper
     * @param mergeFunction The function to merge values conflicting by their key
     * @param <T>           The type of collection item
     * @param <K>           Type of the map key
     * @param <U>           Type of the map value
     * @return A new map
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {

        return collection.stream().collect(Collectors.toConcurrentMap(keyMapper, valueMapper, mergeFunction));
    }

    /**
     * Safe parsing of string to some {@link Number} value.
     *
     * @param x     The string value
     * @param clazz The expected class of Number object
     * @param <S>   The object type
     * @return The resulted parsed value or null if parsing was unsuccessful
     */
    public static <S extends Number> S parse(String x, Class<S> clazz) {
        S v = null;
        try {
            v = inst(clazz, new Class<?>[]{String.class}, x);
        } catch (FunctorException | IllegalArgumentException ignored) {
        }
        return v;
    }

    /**
     * Instantiation of objects.
     *
     * @param clazz      the class to instantiate
     * @param paramTypes the constructor arguments
     * @param args       Any arguments provided
     * @param <S>        The object type
     * @return The resulted new object
     */
    public static <S> S inst(Class<? extends S> clazz, Class<?>[] paramTypes, Object... args) {
        return FactoryUtils.instantiateFactory(clazz, paramTypes, args).create();
    }

    /**
     * Generates 'GUID' (some unique string value).
     *
     * @return String with GUID
     */
    public static String guid() {
        return UUID.randomUUID().toString();
    }

    /**
     * A short-cut for creation of {@link BigDecimal} from a string value.
     *
     * @param numberAsStr String representation for decimal
     * @return a new {@link BigDecimal}
     */
    public static BigDecimal d(String numberAsStr) {
        return new BigDecimal(numberAsStr);
    }

    /**
     * A short-cut for creation of {@link BigDecimal} from double value
     *
     * @param value Double value
     * @return a new {@link BigDecimal}
     */
    public static BigDecimal d(double value) {
        return d(Double.toString(value));
    }

    /**
     * A short-cut for setting a scale for {@link BigDecimal} using the standard
     * {@link RoundingMode#HALF_UP} mode (so called 'school' rounding).
     *
     * @param d     A decimal value
     * @param scale The scale
     * @return New {@link BigDecimal} in the specified scale
     */
    public static BigDecimal scale(BigDecimal d, int scale) {
        return d.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * A short-cut for division with a specific scale for the result.
     *
     * @param a     A divided value
     * @param b     A divisor
     * @param scale The scale
     * @return New {@link BigDecimal} in the specified scale
     */
    public static BigDecimal div(BigDecimal a, BigDecimal b, int scale) {

        return a.divide(b, scale, RoundingMode.HALF_UP);
    }

    /**
     * Clones the specified object if allowed
     *
     * @param o   The value
     * @param <S> Object type
     * @return Clone (or null, if the original object was null)
     */
    @SuppressWarnings("unchecked")
    public static <S> S cloneObject(Object o) {
        return (S) org.apache.commons.lang3.ObjectUtils.cloneIfPossible(o);
    }


    /**
     * A short-cut for the often used variant
     *
     * @param s A string
     * @return true, if the string is NOT empty
     */
    public static boolean notEmpty(Object s) {
        return !isEmpty(s);
    }

    /**
     * A short-cut for the universal function to check emptiness
     * of the given object regardless its type.
     *
     * @param s A string
     * @return true, if the string is empty
     */
    public static boolean isEmpty(Object s) {
        return org.apache.commons.lang3.ObjectUtils.isEmpty(s);
    }

    /**
     * Determines whether the given collection contains specified objects or
     * not. The parameter all must be 'true' if we expect all strings to
     * be included in the source collection, otherwise it is 'false' (That means
     * at least one must be included).
     *
     * @param coll  A source collection
     * @param all   true, if all inclusion are expected, or false, if at least
     *              one.
     * @param items Expected strings
     * @return true, if the given collection contains the specified items
     * according to the condition 'conjunction'.
     */
    @SafeVarargs
    public static <S> boolean contains(Collection<S> coll, boolean all, S... items) {
        Set<S> s = set(items);
        return all ?
                coll.containsAll(list(items)) :
                coll.stream().parallel().anyMatch(s::contains);
    }

    /**
     * Calculates a total value for the specified stream. The given mapper
     * function provides a conversion operation from an object of the type S to
     * a {@link BigDecimal} value.
     *
     * @param stream The stream to use
     * @param mapper The mapper function
     * @param <S>    The supposed type of objects in the stream
     * @return The calculated result of decimal value
     */
    public static <S> BigDecimal total(Stream<S> stream, Function<? super S, BigDecimal> mapper) {
        return stream.map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates a total value for the specified stream. The given mapper
     * function provides a conversion operation from an object of the type S to
     * a {@link BigDecimal} value.
     *
     * @param list   The list of objects
     * @param mapper The mapper function
     * @param <S>    The supposed type of objects in the stream
     * @return The calculated result of decimal value
     */
    public static <S> BigDecimal total(List<S> list, Function<? super S, BigDecimal> mapper) {

        return total(list.stream(), mapper);
    }

    /**
     * Finds the keys that do not have values.
     *
     * @param <T>  The type of keys
     * @param map  The map
     * @param keys The list of keys
     * @return first key without value or <code>null</code>
     */
    public static <T> Set<T> getEmptyKeys(Map<T, ?> map, Collection<T> keys) {
        return keys.stream().filter(key -> map.get(key) == null).collect(Collectors.toSet());
    }

    /**
     * Executes something specified in the callback but ignores any exceptions
     * and writes them to the error log
     *
     * @param callback The callback to use
     * @param params   A set of parameters
     */
    public static void runIgnored(Consumer<Object[]> callback, Object... params) {
        try {
            callback.accept(params);
        } catch (Throwable ex) {
            error("Ignored error: {}", nullSafe(ex.getMessage()));
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
     * @param c A clock
     */
    public static void setClock(Clock c) {
        clock = c;
    }

    /**
     * The current time. It is always represented in UTC.
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
     * @param dateTime Date time in Java 8 format
     * @return The resulted old Date object
     */
    public static Date date(ZonedDateTime dateTime) {
        return Date.from(dateTime.toInstant());
    }

    /**
     * Transforms the given calendar value to a zoned date from the default
     * (UTC) time zone.
     *
     * @param calendar Some calendar value
     * @return A zoned date-time
     */
    public static ZonedDateTime date(Calendar calendar) {
        return ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
    }

    /**
     * Transforms the given zoned date-time object to a gregorian calendar
     *
     * @param dateTime A date-time object
     * @return A calendar object
     */
    public static Calendar calendar(ZonedDateTime dateTime) {
        return GregorianCalendar.from(dateTime);
    }

    /**
     * Transforms an old date object to a zoned date using the default (UTC)
     * time zone
     *
     * @param oldDate Some old date
     * @return A zoned object
     */
    public static ZonedDateTime date(Date oldDate) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(oldDate.getTime()), DEFAULT_TIMEZONE);
    }

    /**
     * Checks the given date-time object was in the past
     *
     * @param z Some date-time object
     * @return true, if the given date was in the past comparing with the
     * present time
     */
    public static boolean inPast(ZonedDateTime z) {
        return z.isBefore(now());
    }

    ////////////////////////////////////////////////////////////

    /**
     * Instant logging (usually for testing)
     *
     * @param msg       A log message
     * @param arguments A set of argument of the message
     */
    public static void log(String msg, Object... arguments) {
        LoggerFactory.getLogger(BaseParent.class).info(msg, arguments);
    }

    /**
     * Instant logging for an error (usually for testing)
     *
     * @param msg       A log message
     * @param arguments A set of argument of the message
     */
    public static void error(String msg, Object... arguments) {
        LoggerFactory.getLogger(BaseParent.class).error(msg, arguments);
    }

    /**
     * The progress bar of expectations
     */
    private static final Set<Integer> PERCENTS = set(10, 25, 50, 75, 90);

    /**
     * Expects satisfaction of the callback condition during the specified number of seconds and
     * checks the condition on each iteration.
     *
     * @param location    The short description for the wait condition (useful in logs)
     * @param secs        The number of seconds
     * @param sleepTime   The sleep time in milliseconds
     * @param logProgress true, if it is required to log the progress
     * @param callback    The callback
     * @param args        The arguments
     * @return true, if the number of attempts has been exceeded
     */
    public static boolean waitCondition(String location, int secs, int sleepTime, boolean logProgress,
                                        Function<Object[], Boolean> callback, Object... args) {

        int counter = 0;
        Set<Integer> s = new HashSet<>(PERCENTS);

        while (!callback.apply(args)) {

            int tick = (100 * counter / (secs * 1000));
            List<Integer> r = filter(s, i -> i < tick);

            if (!r.isEmpty()) {
                if (logProgress) {
                    log("{}: wait progress: {} %", location, r.get(0));
                }
                r.forEach(s::remove);
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
     * A variant {@link #waitCondition(String, int, int, boolean, Function, Object...)} when
     * the sleep internal is fixed and equals to 500 ms.
     *
     * @param location    The short description of the place where this wait condition is
     *                    used (for logging purposes)
     * @param secs        The number of seconds
     * @param logProgress true, if it is required to log the progress
     * @param callback    The callback
     * @param args        The arguments
     * @return true, if the number of attempts has been exceeded
     */
    public static boolean waitCondition(String location, int secs, boolean logProgress, Function<Object[], Boolean> callback,
                                        Object... args) {
        return waitCondition(location, secs, 500, logProgress, callback, args);
    }

    /**
     * Reading a file from the class path
     *
     * @param path A path to the file's location
     * @return The content as a string
     */
    public static String readAsString(String path) {
        return utf8(readAsBytes(path));
    }

    /**
     * Reading a file from the class path
     *
     * @param path A path to the file's location
     * @return The content as a byte array
     */
    public static byte[] readAsBytes(String path) {

        try {
            return IOUtils.toByteArray(new ClassPathResource(path).getInputStream());
        } catch (IOException ex) {
            throw new ApplicationException(ex);
        }
    }

    public static String formatDateTime(ZonedDateTime dateTime, String pattern) {
        return nullSafe(dateTime, d -> DateTimeFormatter.ofPattern(pattern).format(d)).orElse(null);
    }

    /**
     * Hash string with sha256 algorithm
     *
     * @param s The given string
     * @return hashed string
     */
    public static String sha256(String s) {
        return digest(s, "SHA-256");
    }

    /**
     * Generates the digest of the given string using the given algorithm
     *
     * @param s         The string
     * @param algorithm The algorithm
     * @return The resulted digest
     */
    public static String digest(String s, String algorithm) {

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(s.getBytes());
            return Hex.encodeHexString(md.digest()).toLowerCase(Locale.getDefault());
        } catch (NoSuchAlgorithmException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * This method convert the given collection of objects to a set of some
     * unique key attributes. It can be used to fast check the object collection
     * consist of required ones using just their keys.
     *
     * @param coll     A collection
     * @param callback The callback for extracting the key from the given object
     * @param <S>      The type of the object in the collection
     * @param <K>      The type of keys
     * @return A set of key attributes
     */
    public static <S, K> Set<K> extract(Collection<S> coll, Function<S, K> callback) {
        return set(coll.stream().map(callback));
    }

    /**
     * Parses the string enum value as a enum but with suppressing all exceptions
     *
     * @param clazz    A enum class
     * @param strValue A string value
     * @param <S>      The enum type
     * @return The parsed value
     */
    public static <S extends Enum<S>> S parseEnum(Class<S> clazz, String strValue) {
        return ParseUtils.parseEnum(clazz, strValue);
    }

    /**
     * Parses the given string as the date according to the pattern. If nothing is parsed, the default value
     * is used.
     *
     * @param strValue The value to parse
     * @param pattern  The pattern
     * @return The resulted parsed value
     */
    public static Optional<LocalDateTime> parseLocal(String strValue, String pattern) {
        return nullSafe(strValue, s -> ParseUtils.parseLocal(s, pattern));
    }

    /**
     * Convers the given local date to the default zone 00:00 time.
     *
     * @param date The original date
     * @return The resulted zoned date time object
     */
    public static ZonedDateTime fromLocal(LocalDate date) {
        return ZonedDateTime.of(date, LocalTime.MIDNIGHT, DEFAULT_TIMEZONE);
    }

    /**
     * Convert a calendar to a local date object
     *
     * @param calendar The calendar
     * @return The resulted local date
     */
    public static LocalDate toLocal(Calendar calendar) {
        return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

}
