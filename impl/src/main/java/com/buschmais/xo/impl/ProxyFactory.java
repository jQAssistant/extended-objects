package com.buschmais.xo.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.proxy.InstanceInvocationHandler;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * The factory provides methods for creating dynamic proxies and unwrapping the them.
 */
public class ProxyFactory {

    private final InterceptorFactory interceptorFactory;
    private final ClassLoader classLoader;

    /**
     * Constructor.
     *
     * @param interceptorFactory The {@link com.buschmais.xo.spi.interceptor.InterceptorFactory}.
     * @param classLoader        The class loader.
     */
    public ProxyFactory(InterceptorFactory interceptorFactory, ClassLoader classLoader) {
        this.interceptorFactory = interceptorFactory;
        this.classLoader = classLoader;
    }

    /**
     * Creates a proxy instance.
     *
     * @param invocationHandler The {@link java.lang.reflect.InvocationHandler}.
     * @param types             The interface the proxy will implement.
     * @param baseType          The base interface type the proxy will implement.
     * @param <Instance>        The instance type.
     * @return The instance.
     */
    public <Instance> Instance createInstance(InvocationHandler invocationHandler, Class<?>[] types, Class<?> baseType) {
        Class<?>[] effectiveTypes = new Class<?>[types.length + 1];
        effectiveTypes[0] = baseType;
        int i = 1;
        for (Class<?> type : types) {
            effectiveTypes[i++] = type;
        }
        Instance instance = (Instance) Proxy.newProxyInstance(classLoader, effectiveTypes, invocationHandler);
        return interceptorFactory.addInterceptor(instance, effectiveTypes);
    }

    /**
     * Extracts the {@link com.buschmais.xo.impl.proxy.InstanceInvocationHandler} from a proxy instance.
     *
     * @param instance        The proxy instance.
     * @param <DatastoreType> The expected datastore type of the {@link com.buschmais.xo.impl.proxy.InstanceInvocationHandler}
     * @param <Instance>      The instance type.
     * @return The {@link com.buschmais.xo.impl.proxy.InstanceInvocationHandler}.
     */
    public <DatastoreType, Instance> InstanceInvocationHandler<DatastoreType> getInvocationHandler(Instance instance) {
        Instance effectiveInstance;
        if (interceptorFactory.hasInterceptor(instance)) {
            effectiveInstance = interceptorFactory.removeInterceptor(instance);
        } else {
            effectiveInstance = instance;
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(effectiveInstance);
        if (!(invocationHandler instanceof InstanceInvocationHandler)) {
            throw new XOException("Instance " + instance + " implementing " + Arrays.asList(instance.getClass().getInterfaces()) + " is not a " + InstanceInvocationHandler.class.getName());
        }
        return (InstanceInvocationHandler<DatastoreType>) invocationHandler;
    }


}
