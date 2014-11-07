/**
 * 
 */
package ru.anr.base;

import org.springframework.core.NestedRuntimeException;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */

public class ApplicationException extends NestedRuntimeException {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = -2729182111108737798L;

    /**
     * Constructor
     * 
     * @param msg
     *            Error message
     */
    public ApplicationException(String msg) {

        super(msg);
    }

    /**
     * Constructor
     * 
     * @param msg
     *            An error message
     * @param cause
     *            Root exception
     */
    public ApplicationException(String msg, Throwable cause) {

        super(msg, cause);
    }

    /**
     * Constructor
     * 
     * @param cause
     *            Root exception
     */
    public ApplicationException(Throwable cause) {

        this("", cause);
    }
}
