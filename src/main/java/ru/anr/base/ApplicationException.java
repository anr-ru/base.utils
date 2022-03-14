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


import org.springframework.core.NestedRuntimeException;

/**
 * A wrap for Spring NestedRuntimeException.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
public class ApplicationException extends NestedRuntimeException {

    // Serial ID
    private static final long serialVersionUID = -2729182111108737798L;

    /**
     * The unique code of the error
     */
    private String errorId;

    /**
     * true, if we need to log the full exception stack (or just the error
     * message).
     */
    private boolean logFullStack = true;

    /**
     * Construction of an exception instance
     *
     * @param msg An error message
     */
    public ApplicationException(String msg) {

        super(msg);
    }

    /**
     * Construction of a new exception object with logging full stack property.
     *
     * @param msg          The message
     * @param logFullStack true, if we need the full exception stack to be logged.
     */
    public ApplicationException(String msg, boolean logFullStack) {

        super(msg);
        this.logFullStack = logFullStack;
    }

    /**
     * Construction of an exception instance
     *
     * @param msg   An error message
     * @param cause The root exception
     */
    public ApplicationException(String msg, Throwable cause) {

        super(msg, cause);
    }

    /**
     * Construction of an exception instance
     *
     * @param cause The root exception
     */
    public ApplicationException(Throwable cause) {

        this("", cause);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return the errorId
     */
    public String getErrorId() {

        return errorId;
    }

    /**
     * @return the logFullStack
     */
    public boolean isLogFullStack() {
        return logFullStack;
    }

    /**
     * @param errorId the errorId to set
     */
    public void setErrorId(String errorId) {

        this.errorId = errorId;
    }
}
