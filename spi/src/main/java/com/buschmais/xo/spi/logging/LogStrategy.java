package com.buschmais.xo.spi.logging;

import org.slf4j.Logger;

/**
 * Defines log strategies.
 */
public enum LogStrategy {

    NONE {
        @Override
        public void log(Logger logger, String message) {
        }
    },
    TRACE {
        @Override
        public void log(Logger logger, String message) {
            logger.trace(message);
        }
    },
    DEBUG {
        @Override
        public void log(Logger logger, String message) {
            logger.debug(message);
        }
    },
    INFO {
        @Override
        public void log(Logger logger, String message) {
            logger.info(message);
        }
    },
    WARN {
        @Override
        public void log(Logger logger, String message) {
            logger.warn(message);
        }
    },
    ERROR {
        @Override
        public void log(Logger logger, String message) {
            logger.error(message);
        }
    };

    public abstract void log(Logger logger, String message);
}
