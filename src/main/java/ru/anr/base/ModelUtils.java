/*
 * Copyright 2014-2022 the original author or authors.
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
