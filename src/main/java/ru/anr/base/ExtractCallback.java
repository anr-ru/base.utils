/**
 * 
 */
package ru.anr.base;

/**
 * A callback for extracting some key attribute from the given object.
 *
 *
 * @author Alexey Romanchuk
 * @created Aug 27, 2019
 *
 * @param <S>
 *            The type of the object
 * @param <K>
 *            The type of the key
 */
@FunctionalInterface
public interface ExtractCallback<S, K> {

    /**
     * Extracts a key attribute from the given object
     * 
     * @param object
     *            The object
     * @return The key
     */
    K extractKey(S object);
}
