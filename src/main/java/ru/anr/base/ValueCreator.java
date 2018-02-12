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

/**
 * An auxiliary interface for {@link BaseParent#nullSafe(Object, ValueCreator)}.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 22, 2017
 *
 * @param <S>
 *            The result value
 * @param <V>
 *            The original value
 */
@FunctionalInterface
public interface ValueCreator<S, V> {

    /**
     * Creates a new value of the S type using the given value
     * 
     * @param v
     *            The value to use
     * @return A new model
     */
    S newValue(V v);
}
