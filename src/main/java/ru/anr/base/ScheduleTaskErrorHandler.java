/**
 * 
 */
package ru.anr.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;
import org.springframework.util.ReflectionUtils;

/**
 * An error handler for scheduled tasks.
 *
 *
 * @author Alexey Romanchuk
 * @created Oct 28, 2019
 *
 */

public class ScheduleTaskErrorHandler implements ErrorHandler {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskErrorHandler.class);

    @Override
    public void handleError(Throwable t) {

        if (t instanceof ApplicationException) {
            ApplicationException ex = (ApplicationException) t;
            if (ex.isLogFullStack()) {
                logger.error("Scheduler error: " + ex.getMessage(), ex);
            } else {
                logger.error("Scheduler error: {}", ex.getMessage());
            }
        } else {
            Throwable root = new ApplicationException(t).getRootCause();
            logger.error("Scheduler error: " + root.getMessage(), root);
        }
        ReflectionUtils.rethrowRuntimeException(t);
    }
}
