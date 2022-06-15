package ru.anr.base;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Some utils for extracting properties from bean models
 *
 * @author Alexey Romanchuk
 * @created Jun 15, 2022
 */
public class ModelUtils extends BaseParent {

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
    public static <T, V, R> R extractProperty(T model, Function<T, V> valueExtractor, Function<V, R> valueCallback) {
        return nullSafe(model, d -> nullSafe(valueExtractor.apply(d), valueCallback).orElse(null)).orElse(null);
    }

    /**
     * Converts the collection of enums to a list of their
     *
     * @param coll The collection of enums
     * @param <S>  The type of Enum object
     * @return The list of string names
     */
    public static <S extends Enum<S>> List<String> enumToStr(Collection<S> coll) {
        return coll.stream().map(Enum::name).collect(Collectors.toList());
    }
}
