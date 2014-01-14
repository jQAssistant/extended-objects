package com.buschmais.cdo.impl.interceptor;

import java.util.concurrent.locks.ReentrantLock;

public class ConcurrencyInterceptor implements CdoInterceptor {

    private ReentrantLock lock;

    public ConcurrencyInterceptor() {
        lock = new ReentrantLock();
    }

    @Override
    public Object invoke(InvocationContext invocationContext) throws Throwable {
        lock.lock();
        try {
            return invocationContext.proceed();
        } finally {
            lock.unlock();
        }
    }
}
