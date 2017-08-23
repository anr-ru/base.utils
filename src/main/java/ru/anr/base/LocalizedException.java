/**
 * 
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
