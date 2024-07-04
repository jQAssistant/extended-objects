package com.buschmais.xo.neo4j.embedded.api;

import lombok.RequiredArgsConstructor;
import org.neo4j.logging.Log;
import org.neo4j.logging.LogProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;

/**
 * {@link LogProvider} for Slf4j.
 */
@RequiredArgsConstructor(access = PRIVATE)
public class Slf4jLogProvider implements LogProvider {

    public static final Slf4jLogProvider INSTANCE = new Slf4jLogProvider();

    @Override
    public Log getLog(Class<?> loggingClass) {
        return new Slf4jLog(LoggerFactory.getLogger(loggingClass));
    }

    @Override
    public Log getLog(String name) {
        return new Slf4jLog(LoggerFactory.getLogger(name));
    }

    @RequiredArgsConstructor
    private static final class Slf4jLog implements Log {

        private final Logger logger;

        @Override
        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public void debug(String message) {
            logger.debug(message);
        }

        @Override
        public void debug(String message, Throwable throwable) {
            logger.debug(message, throwable);
        }

        @Override
        public void debug(String format, Object... arguments) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format(format, arguments));
            }
        }

        @Override
        public void info(String message) {
            logger.info(message);
        }

        @Override
        public void info(String message, Throwable throwable) {
            logger.info(message, throwable);
        }

        @Override
        public void info(String format, Object... arguments) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format(format, arguments));
            }
        }

        @Override
        public void warn(String message) {
            logger.warn(message);
        }

        @Override
        public void warn(String message, Throwable throwable) {
            logger.warn(message, throwable);
        }

        @Override
        public void warn(String format, Object... arguments) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format(format, arguments));
            }
        }


        @Override
        public void error(String message) {
            logger.error(message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            logger.error(message, throwable);
        }

        @Override
        public void error(String format, Object... arguments) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format(format, arguments));
            }
        }
    }
}
