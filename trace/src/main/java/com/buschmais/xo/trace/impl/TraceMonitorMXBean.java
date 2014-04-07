package com.buschmais.xo.trace.impl;

import javax.management.openmbean.CompositeData;
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
