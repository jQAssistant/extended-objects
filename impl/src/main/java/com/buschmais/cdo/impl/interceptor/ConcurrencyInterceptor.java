package com.buschmais.cdo.impl.interceptor;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ConcurrencyMode;

import java.util.concurrent.locks.ReentrantLock;

public class ConcurrencyInterceptor implements CdoInterceptor {

    private ConcurrencyMode concurrencyMode;
    private ReentrantLock lock;

    public ConcurrencyInterceptor(ConcurrencyMode concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
        lock = new ReentrantLock();
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
                throw new CdoException("Unsupported concurrency mode " + concurrencyMode);
        }
    }
}

