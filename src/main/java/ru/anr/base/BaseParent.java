/**
 *
 */
package ru.anr.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

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
    public void inject(Object target, String fieldName, Object field) {

        Field f = ReflectionUtils.findField(target.getClass(), fieldName);
        Assert.notNull(f);

        ReflectionUtils.makeAccessible(f);
        ReflectionUtils.setField(f, target, field);
    }
}
