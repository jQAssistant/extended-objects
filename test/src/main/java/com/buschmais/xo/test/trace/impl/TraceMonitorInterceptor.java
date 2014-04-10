package com.buschmais.xo.test.trace.impl;

import com.buschmais.xo.spi.interceptor.InvocationContext;
import com.buschmais.xo.spi.interceptor.XOInterceptor;

/**
 * An interceptor for tracing calls on XO SPI interfaces.
 */
public class TraceMonitorInterceptor implements XOInterceptor {

    private TraceMonitor traceMonitor;

    public TraceMonitorInterceptor(TraceMonitor traceMonitor) {
        this.traceMonitor = traceMonitor;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Object invoke(InvocationContext invocationContext) throws Throwable {
        traceMonitor.log("Entering '" + invocationContext.getMethod().toString() + "'");
        long start = System.currentTimeMillis();
        try {
            return invocationContext.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            traceMonitor.recordInvocation(invocationContext.getMethod(), time);
            traceMonitor.log("Leaving '" + invocationContext.getMethod().toString() + "' [" + time + "ms]");
        }
    }
}
