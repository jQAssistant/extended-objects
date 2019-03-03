package com.buschmais.xo.trace.impl;

import java.beans.ConstructorProperties;
import java.lang.reflect.Method;
import java.util.*;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.bootstrap.XOUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link TraceMonitorMXBean}.
 */
public class TraceMonitor implements TraceMonitorMXBean {

    /**
     * Method invocation statistics.
     */
    public static final class MethodStatistics {

        private String method;
        private long invocations;
        private long totalTime;

        @ConstructorProperties({ "invocations", "totalTime" })
        private MethodStatistics(String method, long invocations, long totalTime) {
            this.method = method;
            this.invocations = invocations;
            this.totalTime = totalTime;
        }

        public String getMethod() {
            return method;
        }

        public long getInvocations() {
            return invocations;
        }

        public void setInvocations(long invocations) {
            this.invocations = invocations;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(long totalTime) {
            this.totalTime = totalTime;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceMonitor.class);

    private Map<Method, MethodStatistics> statistics = new HashMap<>();

    /**
     * The levels which can be used for tracing.
     */
    public enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR;
    }

    private XOUnit xoUnit;

    private Level level = Level.INFO;

    /**
     * Constructor.
     *
     * @param xoUnit
     *            The {@link com.buschmais.xo.api.bootstrap.XOUnit} to be monitored.
     */
    public TraceMonitor(XOUnit xoUnit) {
        this.xoUnit = xoUnit;
    }

    public XOUnit getXOUnit() {
        return xoUnit;
    }

    @Override
    public void setLevel(String level) {
        this.level = Level.valueOf(level.toUpperCase());
    }

    @Override
    public String getLevel() {
        return level.name();
    }

    @Override
    public synchronized void reset() {
        this.statistics.clear();
    }

    @Override
    public synchronized List<MethodStatistics> getMethodStatistics() {
        List<MethodStatistics> methodStatisticses = new ArrayList<>(statistics.values());
        Collections.sort(methodStatisticses, new Comparator<MethodStatistics>() {
            @Override
            public int compare(MethodStatistics o1, MethodStatistics o2) {
                if (o1.getTotalTime() == o2.getTotalTime()) {
                    return 0;
                }
                // Sort descending
                return (o2.getTotalTime() < o1.getTotalTime()) ? -1 : 1;
            }
        });
        return methodStatisticses;
    }

    /**
     * Log a message using the configured log level.
     *
     * @param message
     *            The message.
     */
    public void log(String message) {
        switch (level) {
        case TRACE:
            LOGGER.trace(message);
            break;
        case DEBUG:
            LOGGER.debug(message);
            break;
        case INFO:
            LOGGER.info(message);
            break;
        case WARN:
            LOGGER.warn(message);
            break;
        case ERROR:
            LOGGER.error(message);
            break;
        default:
            throw new XOException("Unsupported log log level " + level);
        }
    }

    public synchronized void recordInvocation(Method method, long time) {
        MethodStatistics methodStatistics = statistics.get(method);
        if (methodStatistics == null) {
            methodStatistics = new MethodStatistics(method.toGenericString(), 0, 0);
            statistics.put(method, methodStatistics);
        }
        methodStatistics.setInvocations(methodStatistics.getInvocations() + 1);
        methodStatistics.setTotalTime(methodStatistics.getTotalTime() + time);
    }
}
