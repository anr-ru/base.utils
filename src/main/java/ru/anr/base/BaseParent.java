/*
 * Copyright 2014-2023 the original author or authors.
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

import com.jamesmurty.utils.XMLBuilder;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
 * The base class which contains a set of useful functions and short-cuts.
 * <p>
 * The main idea is to reduce the number of code lines in projects when using this
 * class as the parent.
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
     * A short-cut method for building a list from a collection
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
     * the annoying {@link UnsupportedEncodingException} exception.
     *
     * @param s        The original string
     * @param encoding The encoding
     * @return The resulted array of bytes
     */
    public static byte[] bytes(String s, Charset encoding) {
        return s.getBytes(encoding);
    }

    /**
     * Converts a string to its utf-8 bytes
     *
     * @param s The original string
     * @return The resulted array of bytes
     */
    public static byte[] utf8(String s) {
        return bytes(s, DEFAULT_CHARSET);
    }

    /**
     * Converts bytes to a string with the utf-8 encoding.
     *
     * @param b The array of bytes
     * @return The resulted string
     */
    public static String utf8(byte[] b) {
        return new String(b, DEFAULT_CHARSET);
    }


    /**
     * Converts the given bytes to Base64 string representation.
     *
     * @param bytes The bytes to convert
     * @return The resulted Base64-encoded string
     */
    public static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Converts the given base64-encoded string back to bytes
     *
     * @param s The Base64-encoded string
     * @return The resulted bytes
     */
    public static byte[] base64(String s) {
        return Base64.getDecoder().decode(s);
    }

    /**
     * A null-safe creation of lists.
     *
     * @param l   The original, possibly, null value
     * @param <S> The type of elements
     * @return A new list with elements from the original one
     */
    public static <S> List<S> list(List<S> l) {
        return nullSafe(l, ArrayList::new).orElse(new ArrayList<>());
    }

    /**
     * A simpler shortcut for converting a stream to a list.
     *
     * @param stream The stream
     * @param <S>    The object type
     * @return The resulted list
     */
    public static <S> List<S> list(Stream<S> stream) {
        return stream.collect(Collectors.toList());
    }

    /**
     * A shortcut for the creation of a set from an array.
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
     * A simpler form for converting streams to sets.
     *
     * @param stream The stream to convert
     * @param <S>    The type of object
     * @return The resulted set
     */
    public static <S> Set<S> set(Stream<S> stream) {
        return stream.collect(Collectors.toSet());
    }

    /**
     * The concatenation of two arrays into one.
     *
     * @param array1 The first array
     * @param array2 The second array
     * @param <S>    The type of arrays' elements
     * @return The resulted array
     */
    @SafeVarargs
    public static <S> S[] concat(S[] array1, S... array2) {
        return ArrayUtils.addAll(array1, array2);
    }

    /**
     * The injection of a private field into the given object. Of course, we are
     * against such a technique in production, but sometimes it is the simplest and transparent
     * way to test some internal things :-).
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
     * Extracts the field's value from the object using the reflection API.
     *
     * @param target    The target object
     * @param fieldName The name of the field
     * @param <S>       The expected type of resulted object. The type casting is unsafe,
     *                  but we suppose the user expects the valid type.
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
     * Finds and invokes the given method for the object. The method can be private.
     *
     * @param target     The target object
     * @param methodName The method to find
     * @param args       The arguments of the method
     * @param <S>        The type of the resulted value
     * @return The resulted value of the method
     */
    @SuppressWarnings("unchecked")
    public static <S> S invoke(Object target, String methodName, Object... args) {

        // Primitive type require the extraction of the type
        Class<?>[] classes = list(args).stream().map(o -> {
            Class<?> primClass = extractPrimitive(o.getClass());
            return primClass == null ? o.getClass() : primClass;
        }).toArray(Class<?>[]::new);

        Method m = ReflectionUtils.findMethod(target.getClass(), methodName, classes);
        Assert.notNull(m, "Method " + methodName + " not defined");

        ReflectionUtils.makeAccessible(m);
        return (S) ReflectionUtils.invokeMethod(m, target, args);
    }

    private static Class<?> extractPrimitive(Class<?> clazz) {
        try {
            return (Class<?>) clazz.getField("TYPE").get(null);
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     * Returns the result of the 'equals' operation even if the arguments are
     * null.
     *
     * @param arg1 The first argument
     * @param arg2 The second argument
     * @return The result of the 'equals' operation. It will be false if one or both
     * arguments are null
     */
    public static boolean safeEquals(Object arg1, Object arg2) {
        return ObjectUtils.nullSafeEquals(arg1, arg2);
    }

    /**
     * Retrieves the first object of the given collection. The operation is null-safe.
     *
     * @param coll The original collection
     * @param <S>  The type of elements
     * @return The found object or null is the collection is empty or null
     */
    public static <S> S first(Collection<S> coll) {
        return CollectionUtils.isEmpty(coll) ? null : coll.iterator().next();
    }

    /**
     * A shortcut method for finding the first item in the given stream. The function is null-safe.
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
     * @param coll      The original collection
     * @param predicate The predicate (can be a lambda expression)
     * @param <S>       The type of collection's items
     * @return The resulted filtered collection
     */
    public static <S> List<S> filter(Collection<S> coll, Predicate<S> predicate) {
        return coll == null ? null : coll.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Adds an item to the given collection if the item is not null.
     *
     * @param coll The collection
     * @param item Some item
     * @param <S>  The type of the item
     * @return The original collection
     */
    public static <S> Collection<S> safeAdd(Collection<S> coll, S item) {
        if (item != null) {
            coll.add(item);
        }
        return coll;
    }

    /**
     * Adds the given value to the collection if the value is not null
     * and return true, if the item was successfully added.
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
     * Sleeps with ignoring {@link InterruptedException}.
     *
     * @param millis The number of milliseconds to sleep
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
     * argument to be a possible null value.
     *
     * @param s The original object (can be null)
     * @return The string which is not null
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
     * Returns the empty {@link Optional} object if the given value is null
     *
     * @param value The value
     * @param <V>   The type of the value
     * @return The resulted {@link Optional} object with the value or empty
     */
    public static <V> Optional<V> nullSafeOp(V value) {
        return (value == null) ? Optional.empty() : Optional.of(value);
    }

    /**
     * A null-safe initialization of object. If the original object is not
     * null, then it is simply returned. But in case of a null value, a new
     * one is created using the given class.
     * <p>
     * This approach allows to safely get an instance of the object
     * even if it is null without doing special checking.
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
     * @param array The pairs (key, value) given as a plain array
     * @param <K>   The type of keys
     * @param <T>   The type of values
     * @param <S>   Type of map values
     * @return The resulted new map
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
     * A shortcut method for simple grouping and converting from a collection to a map.
     * The simplest usage of the function is something like this:
     * <pre>{@code}
     *      List&lt;SampleObject&gt; s = list( new Sample("xxx", 1), new Sample("yyy", 2) );
     *      Map&lt;String, Integer&gt; map = toMap(s, SampleObject::getValue, SampleObject::getIndex);
     *
     *      // The resulted map contains { "xxx": 1, "yyy": 2 }
     *
     * </pre>
     *
     * @param collection  The original collection that needs to be regrouped
     * @param keyMapper   The map key mapper
     * @param valueMapper The map value mapper
     * @param <T>         The type of collection items
     * @param <K>         The type of map keys
     * @param <U>         The type of map values
     * @return The resulted new map
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection,
                                            Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper) {

        return collection.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * A shortcut function to simplify the process of building a map from a
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
     * @param collection    The original collection to group
     * @param keyMapper     The map key mapper
     * @param valueMapper   The map value mapper
     * @param mergeFunction The merge function if values have the same key
     * @param <T>           The type of collection item
     * @param <K>           Type of the map key
     * @param <U>           Type of the map value
     * @return A new map
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper,
                                            BinaryOperator<U> mergeFunction) {
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
     * @param clazz      The class to instantiate
     * @param paramTypes The constructor arguments
     * @param args       Any arguments that need to be provided
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
     * A shortcut for the creation of {@link BigDecimal} from a string value.
     *
     * @param numberAsStr The string representation for a decimal value
     * @return The resulted new {@link BigDecimal} object
     */
    public static BigDecimal d(String numberAsStr) {
        return new BigDecimal(numberAsStr);
    }

    /**
     * A shortcut for the creation of {@link BigDecimal} from a double value
     *
     * @param value The double value
     * @return The resulted new {@link BigDecimal}
     */
    public static BigDecimal d(double value) {
        return d(Double.toString(value));
    }

    /**
     * A shortcut for setting the scale for the given {@link BigDecimal} value using the standard
     * {@link RoundingMode#HALF_UP} mode (so called 'school' rounding).
     *
     * @param d     The decimal value
     * @param scale The scale
     * @return The new {@link BigDecimal} in the specified scale
     */
    public static BigDecimal scale(BigDecimal d, int scale) {
        return d.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * A shortcut for the division operation with a specific scale for the result.
     *
     * @param a     The divided value
     * @param b     The divisor
     * @param scale The scale
     * @return The resulted new {@link BigDecimal} in the specified scale
     */
    public static BigDecimal div(BigDecimal a, BigDecimal b, int scale) {
        return a.divide(b, scale, RoundingMode.HALF_UP);
    }

    /**
     * Clones the specified object if allowed
     *
     * @param o   The object to clone
     * @param <S> The object's type
     * @return The resulted clone (or null, if the original object was null)
     */
    @SuppressWarnings("unchecked")
    public static <S> S cloneObject(Object o) {
        return (S) org.apache.commons.lang3.ObjectUtils.cloneIfPossible(o);
    }


    /**
     * A shortcut for the often used variant
     *
     * @param s The object
     * @return true, if the string representation of the object is NOT empty
     */
    public static boolean notEmpty(Object s) {
        return !isEmpty(s);
    }

    /**
     * A shortcut for the universal function to check emptiness
     * of the given object regardless its type.
     *
     * @param s The object
     * @return true, if the string representation of the object is empty
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
     * @param coll  The source collection
     * @param all   true, if all inclusion are expected, or false, if at least
     *              one.
     * @param items Expected strings
     * @param <S>   The type of arguments
     * @return true, if the given collection contains the specified items
     * according to the condition 'all'.
     */
    @SafeVarargs
    public static <S> boolean contains(Collection<S> coll, boolean all, S... items) {
        Set<S> s = set(items);
        return all ?
                coll.containsAll(list(items)) :
                coll.stream().parallel().anyMatch(s::contains);
    }

    /**
     * Calculates the total value for the specified stream. The given mapper
     * function provides a conversion operation from a stream item of the type S to
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
     * Calculates the total value for the specified stream. The given mapper
     * function provides a conversion operation from a stream item of the type S to
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
        return keys.stream()
                .filter(key -> map.get(key) == null)
                .collect(Collectors.toSet());
    }

    /**
     * Executes something specified in the callback but ignores any exceptions
     * and writes them to the error log
     *
     * @param callback The callback to use
     * @param params   A set of parameters
     * @param <V>      The type of the resulted value
     * @return The result value of the execution (if there were no error or null)
     */
    public static <V> V getRunIgnored(Function<Object[], V> callback, Object... params) {
        try {
            return callback.apply(params);
        } catch (Throwable ex) {
            error("Ignored error: {}", nullSafe(ex.getMessage()));
            return null;
        }
    }

    public static void runIgnored(Consumer<Object[]> callback, Object... params) {
        try {
            callback.accept(params);
        } catch (Throwable ex) {
            error("Ignored error: {}", nullSafe(ex.getMessage()));
        }
    }

    // //////////////////////////// TIME FUNCTIONS ///////////////////////////

    /**
     * The default timezone
     */
    public static final ZoneId DEFAULT_TIMEZONE = ZoneOffset.UTC;

    /**
     * The current clock if we need to use different clocks (in tests, for example)
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
     * The current time. It is always represented in DEFAULT_TIMEZONE. The function returns the fixed
     * clock if it was set.
     *
     * @return The time represented as {@link ZonedDateTime} object
     */
    public static ZonedDateTime now() {
        return (clock == null) ? ZonedDateTime.now(DEFAULT_TIMEZONE) : ZonedDateTime.now(clock);
    }

    /**
     * We have to use old Date object, because Hibernate/JPA does not support
     * Java 8 dates (see <a href="https://java.net/jira/browse/JPA_SPEC-63">...</a> or
     * <a href="https://hibernate.atlassian.net/browse/HHH-8844">...</a>).
     *
     * @param dateTime The date time in Java 8 format
     * @return The resulted old Date object
     */
    public static Date date(ZonedDateTime dateTime) {
        return nullSafe(dateTime, d -> Date.from(d.toInstant())).orElse(null);
    }

    /**
     * Transforms the given calendar value to a zoned date from the default
     * (UTC) time zone.
     *
     * @param calendar Some calendar value
     * @return A zoned date-time
     */
    public static ZonedDateTime date(Calendar calendar) {
        return nullSafe(calendar, c -> ZonedDateTime.ofInstant(c.toInstant(), c.getTimeZone().toZoneId())).orElse(null);
    }

    /**
     * Transforms the given zoned date-time object to a gregorian calendar
     *
     * @param dateTime A date-time object
     * @return A calendar object
     */
    public static Calendar calendar(ZonedDateTime dateTime) {
        return nullSafe(dateTime, GregorianCalendar::from).orElse(null);
    }

    /**
     * Transforms an old date object to a zoned date using the default (UTC)
     * time zone
     *
     * @param oldDate Some old date
     * @return A zoned object
     */
    public static ZonedDateTime date(Date oldDate) {
        return nullSafe(oldDate, d -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.getTime()), DEFAULT_TIMEZONE)).orElse(null);
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

    /**
     * Parses the given string as a local date-time according to the pattern. If nothing is parsed, the null option
     * is returned.
     *
     * @param strValue The value to parse
     * @param pattern  The pattern
     * @return The resulted parsed value
     */
    public static Optional<LocalDateTime> parseLocal(String strValue, String pattern) {
        return nullSafe(strValue, s -> ParseUtils.parseLocal(s, pattern));
    }

    /**
     * Parses the given string as the date according to the pattern. If nothing is parsed, the null option
     * is returned.
     *
     * @param strValue The value to parse
     * @param pattern  The pattern (like 'yyyy-MM-dd' or 'dd.MM.yyyy'
     * @return The resulted parsed value
     */
    public static Optional<LocalDate> parseLocalDate(String strValue, String pattern) {
        return nullSafe(strValue, s -> ParseUtils.parseLocalDate(s, pattern));
    }

    /**
     * Converts the given local date to the default zone 00:00 time.
     *
     * @param date The original date
     * @return The resulted zoned date time object
     */
    public static ZonedDateTime fromLocal(LocalDate date) {
        return nullSafe(date, d -> ZonedDateTime.of(d, LocalTime.MIDNIGHT, DEFAULT_TIMEZONE)).orElse(null);
    }

    /**
     * Converts a calendar to a local date object
     *
     * @param calendar The calendar
     * @return The resulted local date
     */
    public static LocalDate toLocal(Calendar calendar) {
        return nullSafe(calendar, c -> LocalDate.of(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH))
        ).orElse(null);
    }

    /**
     * Converts the given local date to a calendar value.
     *
     * @param date The date
     * @return The resulted calendar
     */
    public static Calendar calendar(LocalDate date) {
        return nullSafe(date, d -> calendar(fromLocal(d))).orElse(null);
    }

    ///////////////////////////////////////////////////////////////////////////

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
     * Reads the file from the given class path
     *
     * @param path The path to the file's location
     * @return The content as a string
     */
    public static String readAsString(String path) {
        return utf8(readAsBytes(path));
    }

    /**
     * Reads the file from the class path
     *
     * @param path The path to the file's location
     * @return The content as a byte array
     */
    public static byte[] readAsBytes(String path) {
        try {
            return IOUtils.toByteArray(new ClassPathResource(path).getInputStream());
        } catch (IOException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Formats the given date/time according to the given pattern.
     *
     * @param dateTime The date/time
     * @param pattern  The string pattern
     * @return The resulted formatted date/time
     */
    public static String formatDateTime(ZonedDateTime dateTime, String pattern) {
        return nullSafe(dateTime, d -> DateTimeFormatter.ofPattern(pattern).format(d)).orElse(null);
    }

    /**
     * Hashes a string with the sha256 algorithm
     *
     * @param s The given string
     * @return The hashed string
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
     * @param clazz    The enum class
     * @param strValue The string value to parse
     * @param <S>      The enum type
     * @return The parsed value as an enum object
     */
    public static <S extends Enum<S>> S parseEnum(Class<S> clazz, String strValue) {
        return ParseUtils.parseEnum(clazz, strValue);
    }

    /**
     * Parses a string via the provided regular expression and returns all found groups as a single string.
     *
     * @param text    The text to parse
     * @param pattern The pattern to use
     * @param groups  The number of groups in the regular expression to extract. If
     *                more than one group is used, all the groups will be combined
     *                as a single string
     * @return The resulted value
     */
    public static String regexp(String text, String pattern, Integer... groups) {
        return ParseUtils.regexp(text, pattern, groups);
    }

    /**
     * Parses a string via the provided regular expression and returns the resulted
     * groups.
     *
     * @param text    The original text
     * @param pattern The pattern to use
     * @param groups  The number of groups in the pattern
     * @return The list of found groups
     */
    public static List<String> regexpGroups(String text, String pattern, Integer... groups) {
        return ParseUtils.regexpGroups(text, pattern, groups);
    }

    /**
     * Parses the provided xml by the specified xpath query
     *
     * @param xml   The XML to use specified as a string
     * @param query The xpath query
     * @return The result as a string
     */
    public static String xpath(String xml, String query) {
        return ParseUtils.xpath(xml, query);
    }

    /**
     * Fills the variables defined as ${var} with the values given in the variable/value map. Also, it replaces
     * single quotes (') with double ones (").
     * <p>
     * For example, this function allows to use XML templates defined in the code as strings.
     *
     * @param template  A string template with variables to replace
     * @param variables The pairs of variable/value to replace
     * @return The resulted string
     */
    public static String fillString(String template, String... variables) {
        return fillString(template, toMap(variables));
    }

    /**
     * Fills the variables defined as ${var} with the values given in the variable/value map. Also, it replaces
     * single quotes (') with double ones (").
     * <p>
     * For example, this function allows to use XML templates defined in the code as strings.
     *
     * @param template A string template with variables to replace
     * @param map      The pairs of variable/value to replace
     * @return The resulted string
     */
    public static String fillString(String template, Map<String, String> map) {
        String rs = template;
        for (Map.Entry<String, String> e : map.entrySet()) {
            rs = rs.replaceAll(("\\$\\{" + e.getKey() + "}"), nullSafe(e.getValue()));
        }
        return rs.replaceAll("'", "\"");
    }

    /**
     * Removes all white spaces and generates the XML.
     *
     * @param rawXML The XML file.
     * @return The resulted XML.
     */
    public static String cleanUpXML(String rawXML) {
        try {
            XMLBuilder xmlResult = XMLBuilder.parse(rawXML);
            StringWriter result = new StringWriter();
            xmlResult.stripWhitespaceOnlyTextNodes().toWriter(result, new Properties());
            return result.getBuffer().toString();
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException | XPathExpressionException ex) {
            throw new ApplicationException(ex);
        }
    }


    /**
     * Shortens the given string to the string having the length that does not exceed the
     * given maxLength parameter.
     *
     * @param str       The string
     * @param maxLength The max length
     * @return The possibly truncated string
     */
    public static String abbreviate(String str, int maxLength) {
        return nullSafe(str, s -> s.substring(0, Math.min(s.length(), maxLength))).orElse(null);
    }

    /**
     * A function for extracting attributes of the model's tree with all necessary not-null checks.
     *
     * @param <T>            The type of original value
     * @param <V>            The type of some field of the dictionary value that can be used as ID
     * @param <R>            The type of final result value
     * @param model          The original model to analyze
     * @param valueExtractor The callback for extracting the value T -&gt; V
     * @param valueCallback  The callback for converting V -&gt; R
     * @return The resulted value of type R
     */
    public static <T, V, R> R extract(T model, Function<T, V> valueExtractor, Function<V, R> valueCallback) {
        return ModelUtils.extractProperty(model, valueExtractor, valueCallback);
    }

    /**
     * Converts the given collection of enums to their string keys
     *
     * @param coll The collection
     * @param <S>  The enum type
     * @return The list of string keys
     */
    public static <S extends Enum<S>> List<String> toStr(Collection<S> coll) {
        return ModelUtils.enumToStr(coll);
    }

    /**
     * Converts the given array of enums to their string keys
     *
     * @param states The states to convert
     * @param <S>  The enum type
     * @return The list of string keys
     */
    @SafeVarargs
    public static <S extends Enum<S>> List<String> toStr(S... states) {
        return toStr(list(states));
    }

}
