package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.proxy.entity.InstanceInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Dirk Mahler on 11.01.14.
 */
public class ProxyFactory {

    private InterceptorFactory interceptorFactory;
    private ClassLoader classLoader;

    public ProxyFactory(InterceptorFactory interceptorFactory, ClassLoader classLoader) {
        this.interceptorFactory = interceptorFactory;
        this.classLoader = classLoader;
    }

    public <Instance> Instance createInstance(InvocationHandler invocationHandler, Set<Class<?>> types, Class<?>... baseTypes) {
        List<Class<?>> effectiveTypes = new ArrayList<>(types.size() + baseTypes.length);
        effectiveTypes.addAll(types);
        effectiveTypes.addAll(Arrays.asList(baseTypes));
        return (Instance) createProxyInstance(invocationHandler, effectiveTypes);
    }

    private Object createProxyInstance(InvocationHandler invocationHandler, List<Class<?>> effectiveTypes) {
        Object instance = Proxy.newProxyInstance(classLoader, effectiveTypes.toArray(new Class<?>[effectiveTypes.size()]), invocationHandler);
        return interceptorFactory.addInterceptor(instance);
    }

    public <Instance> boolean isDatastoreType(Instance instance) {
        if (interceptorFactory.hasInterceptor(instance)) {
            return isDatastoreType(interceptorFactory.removeInterceptor(instance));
        }
        if (Proxy.isProxyClass(instance.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            return invocationHandler instanceof InstanceInvocationHandler;
        }
        return false;
    }

    public <DatastoreType, Instance> InstanceInvocationHandler<DatastoreType> getInvocationHandler(Instance instance) {
        if (interceptorFactory.hasInterceptor(instance)) {
            return getInvocationHandler(interceptorFactory.removeInterceptor(instance));
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!(invocationHandler instanceof InstanceInvocationHandler)) {
            throw new CdoException("Instance " + instance + " implementing " + Arrays.asList(instance.getClass().getInterfaces()) + " is not a " + InstanceInvocationHandler.class.getName());
        }
        return (InstanceInvocationHandler<DatastoreType>) invocationHandler;
    }


}
