package com.buschmais.xo.spi.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class InvocationContext {

    private final Object instance;
    private final Method method;
    private final Object[] args;
    private final XOInterceptor[] chain;
    private int index = 0;

    public InvocationContext(Object instance, Method method, Object[] args, List<? extends XOInterceptor> chain) {
        this.instance = instance;
        this.method = method;
        this.args = args;
        this.chain = chain.toArray(new XOInterceptor[chain.size()]);
    }

    public Object proceed() throws Throwable {
        if (index < chain.length) {
            XOInterceptor xoInterceptor = chain[index];
            index++;
            return xoInterceptor.invoke(this);
        } else {
            try {
                return method.invoke(instance, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return args;
    }

    public Object getInstance() {
        return instance;
    }
}
