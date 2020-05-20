package com.cartravel.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zxc
 */
public abstract class Logging {
    protected static final Logger logger = LoggerFactory.getLogger(Logging.class);
    public static void trace(Object message) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(message));
        }
    }
    public static void debug(Object message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(message));
        }
    }
    public static void info(Object message) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(message));
        }
    }
    public static void warn(Object message) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(message));
        }
    }
    public static void warn(Object message , Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(message) , t);
        }
    }

    public static void error(Object message, Throwable t) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(message), t);
        }
    }
    public static void error(Object message) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(message));
        }
    }
    public static void fatal(Object message, Throwable t) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(message), t);
        }
    }
    public static void fatal(Object message) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(message));
        }
    }


}
