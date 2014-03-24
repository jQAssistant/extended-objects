package com.buschmais.xo.impl.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class InvocationContext {

    private final Object instance;
    private final Method method;
    private final Object[] args;
    private final List<XOInterceptor> chain;
    private int index = 0;

    public InvocationContext(Object instance, Method method, Object[] args, List<XOInterceptor> chain) {
        this.instance = instance;
        this.method = method;
        this.args = args;
        this.chain = chain;
    }

    public Object proceed() throws Throwable {
        if (index < chain.size()) {
            XOInterceptor XOInterceptor = chain.get(index);
            index++;
            return XOInterceptor.invoke(this);
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
