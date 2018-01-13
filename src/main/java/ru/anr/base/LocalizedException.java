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

import java.util.List;

/**
 * Exceptions with internationalized messages.
 *
 *
 * @author Dmitry Philippov
 * @created Aug 21, 2017
 *
 */
public interface LocalizedException {

    /**
     * @return parameters in the order in which they are substituted in the
     *         error message
     */
    List<Object> messageParams();
}
