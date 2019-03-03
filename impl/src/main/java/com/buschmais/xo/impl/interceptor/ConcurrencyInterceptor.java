package com.buschmais.xo.impl.interceptor;

import java.util.concurrent.locks.ReentrantLock;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.interceptor.InvocationContext;
import com.buschmais.xo.spi.interceptor.XOInterceptor;

public class ConcurrencyInterceptor implements XOInterceptor {

    private final ConcurrencyMode concurrencyMode;
    private final ReentrantLock lock;

    public ConcurrencyInterceptor(ConcurrencyMode concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
        lock = new ReentrantLock();
    }

    @Override
    public boolean isActive() {
        return ConcurrencyMode.MULTITHREADED.equals(concurrencyMode);
    }

    @Override
    public Object invoke(InvocationContext invocationContext) throws Throwable {
        switch (concurrencyMode) {
        case SINGLETHREADED:
            return invocationContext.proceed();
        case MULTITHREADED:
            lock.lock();
            try {
                return invocationContext.proceed();
            } finally {
                lock.unlock();
            }
        default:
            throw new XOException("Unsupported concurrency mode " + concurrencyMode);
        }
    }
}
