package com.buschmais.xo.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.buschmais.xo.api.CompositeType;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.proxy.InstanceInvocationHandler;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The factory provides methods for creating dynamic proxies and unwrapping the
 * them.
 */
public class ProxyFactory {

    private final InterceptorFactory interceptorFactory;
    private final ClassLoader classLoader;
    private final Cache<CompositeType, Constructor<?>> classCache = CacheBuilder.newBuilder().maximumSize(512).build();

    /**
     * Constructor.
     *
     * @param interceptorFactory
     *            The
     *            {@link com.buschmais.xo.spi.interceptor.InterceptorFactory}.
     * @param classLoader
     *            The class loader.
     */
    public ProxyFactory(InterceptorFactory interceptorFactory, ClassLoader classLoader) {
        this.interceptorFactory = interceptorFactory;
        this.classLoader = classLoader;
    }

    /**
     * Creates a proxy instance.
     *
     * @param invocationHandler
     *            The {@link java.lang.reflect.InvocationHandler}.
     * @param compositeType
     *            The composite type to create an instance for.
     * @param <Instance>
     *            The instance type.
     * @return The instance.
     */
    public <Instance> Instance createInstance(InvocationHandler invocationHandler, CompositeType compositeType) {
        Class<?>[] classes = compositeType.getClasses();
        Constructor<?> constructor = classCache.getIfPresent(compositeType);
        if (constructor == null) {
            Class<?> type = Proxy.getProxyClass(classLoader, classes);
            try {
                constructor = type.getConstructor(new Class<?>[] { InvocationHandler.class });
            } catch (NoSuchMethodException e) {
                throw new XOException("Cannot find constructor for " + compositeType, e);
            }
            classCache.put(compositeType, constructor);
        }
        Instance instance;
        try {
            instance = (Instance) constructor.newInstance(invocationHandler);
        } catch (Exception e) {
            throw new XOException("Cannot create instance of  " + compositeType, e);
        }
        return interceptorFactory.addInterceptor(instance, classes);
    }

    /**
     * Extracts the
     * {@link com.buschmais.xo.impl.proxy.InstanceInvocationHandler} from a
     * proxy instance.
     *
     * @param instance
     *            The proxy instance.
     * @param <DatastoreType>
     *            The expected datastore type of the
     *            {@link com.buschmais.xo.impl.proxy.InstanceInvocationHandler}
     * @param <Instance>
     *            The instance type.
     * @return The {@link com.buschmais.xo.impl.proxy.InstanceInvocationHandler}
     *         .
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
            throw new XOException("Instance " + instance + " implementing " + Arrays.asList(instance.getClass().getInterfaces()) + " is not a "
                    + InstanceInvocationHandler.class.getName());
        }
        return (InstanceInvocationHandler<DatastoreType>) invocationHandler;
    }

}
