package com.buschmais.xo.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.buschmais.xo.impl.proxy.entity.InstanceInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
    public <Instance> Instance createInstance(InvocationHandler invocationHandler, Set<Class<?>> types, Class<?> baseType) {
        List<Class<?>> effectiveTypes = new ArrayList<>(types.size() + 1);
        effectiveTypes.addAll(types);
        effectiveTypes.add(baseType);
        Object instance = Proxy.newProxyInstance(classLoader, effectiveTypes.toArray(new Class<?>[effectiveTypes.size()]), invocationHandler);
        return (Instance) interceptorFactory.addInterceptor(instance);
    }

    /**
     * Extracts the {@link com.buschmais.xo.impl.proxy.entity.InstanceInvocationHandler} from a proxy instance.
     *
     * @param instance        The proxy instance.
     * @param <DatastoreType> The expected datastore type of the {@link com.buschmais.xo.impl.proxy.entity.InstanceInvocationHandler}
     * @param <Instance>      The instance type.
     * @return The {@link com.buschmais.xo.impl.proxy.entity.InstanceInvocationHandler}.
     */
    public <DatastoreType, Instance> InstanceInvocationHandler<DatastoreType> getInvocationHandler(Instance instance) {
        if (interceptorFactory.hasInterceptor(instance)) {
            return getInvocationHandler(interceptorFactory.removeInterceptor(instance));
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!(invocationHandler instanceof InstanceInvocationHandler)) {
            throw new XOException("Instance " + instance + " implementing " + Arrays.asList(instance.getClass().getInterfaces()) + " is not a " + InstanceInvocationHandler.class.getName());
        }
        return (InstanceInvocationHandler<DatastoreType>) invocationHandler;
    }


}
