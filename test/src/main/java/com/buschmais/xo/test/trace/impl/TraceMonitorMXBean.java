package com.buschmais.xo.test.trace.impl;

import java.util.List;

/**
 * MXBean interface for tracing invocations via JMX.
 */
public interface TraceMonitorMXBean {

    void setLevel(String level);

    String getLevel();

    void reset();

    List<TraceMonitor.MethodStatistics> getMethodStatistics();
}
