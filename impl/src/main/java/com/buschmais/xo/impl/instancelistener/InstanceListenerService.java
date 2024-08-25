package com.buschmais.xo.impl.instancelistener;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.annotation.*;
import com.buschmais.xo.spi.reflection.ClassHelper;

/**
 * Provides functionality to invoke life cycle methods on instance listeners.
 */
public class InstanceListenerService {

    private Map<Object, Set<Method>> postCreateMethods;
    private Map<Object, Set<Method>> preUpdateMethods;
    private Map<Object, Set<Method>> postUpdateMethods;
    private Map<Object, Set<Method>> preDeleteMethods;
    private Map<Object, Set<Method>> postDeleteMethods;
    private Map<Object, Set<Method>> postLoadMethods;

    /**
     * Constructor.
     *
     * @param instanceListenerTypes
     *     The statically registered instance listener types.
     */
    public InstanceListenerService(List<? extends Class<?>> instanceListenerTypes) {
        postCreateMethods = new IdentityHashMap<>();
        preUpdateMethods = new IdentityHashMap<>();
        postUpdateMethods = new IdentityHashMap<>();
        preDeleteMethods = new IdentityHashMap<>();
        postDeleteMethods = new IdentityHashMap<>();
        postLoadMethods = new IdentityHashMap<>();
        for (Class<?> listenerType : instanceListenerTypes) {
            Object instanceListener = ClassHelper.newInstance(listenerType);
            registerInstanceListener(instanceListener);
        }
    }

    /**
     * Invoke all post-create methods for the given instance.
     *
     * @param instance
     *     The instance.
     * @param <T>
     *     The instance type.
     */
    public <T> void postCreate(T instance) {
        invoke(postCreateMethods, instance);
    }

    /**
     * Invoke all pre-update methods for the given instance.
     *
     * @param instance
     *     The instance.
     * @param <T>
     *     The instance type.
     */
    public <T> void preUpdate(T instance) {
        invoke(preUpdateMethods, instance);
    }

    /**
     * Invoke all post-update methods for the given instance.
     *
     * @param instance
     *     The instance.
     * @param <T>
     *     The instance type.
     */
    public <T> void postUpdate(T instance) {
        invoke(postUpdateMethods, instance);
    }

    /**
     * Invoke all pre-delete methods for the given instance.
     *
     * @param instance
     *     The instance.
     * @param <T>
     *     The instance type.
     */
    public <T> void preDelete(T instance) {
        invoke(preDeleteMethods, instance);
    }

    /**
     * Invoke all post-delete methods for the given instance.
     *
     * @param instance
     *     The instance.
     * @param <T>
     *     The instance type.
     */
    public <T> void postDelete(T instance) {
        invoke(postDeleteMethods, instance);
    }

    /**
     * Invoke all post-load methods for the given instance.
     *
     * @param instance
     *     The instance.
     * @param <T>
     *     The instance type.
     */
    public <T> void postLoad(T instance) {
        invoke(postLoadMethods, instance);
    }

    /**
     * Add an instance listener instance.
     *
     * @param instanceListener
     *     The instance listener instance.
     */
    public void registerInstanceListener(Object instanceListener) {
        for (Method method : instanceListener.getClass()
            .getMethods()) {
            evaluateMethod(instanceListener, PostCreate.class, method, postCreateMethods);
            evaluateMethod(instanceListener, PreUpdate.class, method, preUpdateMethods);
            evaluateMethod(instanceListener, PostUpdate.class, method, postUpdateMethods);
            evaluateMethod(instanceListener, PreDelete.class, method, preDeleteMethods);
            evaluateMethod(instanceListener, PostDelete.class, method, postDeleteMethods);
            evaluateMethod(instanceListener, PostLoad.class, method, postLoadMethods);
        }
    }

    /**
     * Evaluates a method if an annotation is present and if true adds to the map of
     * life cycle methods.
     *
     * @param listener
     *     The listener instance.
     * @param annotation
     *     The annotation to check for.
     * @param method
     *     The method to evaluate.
     * @param methods
     *     The map of methods.
     */
    private void evaluateMethod(Object listener, Class<? extends Annotation> annotation, Method method, Map<Object, Set<Method>> methods) {
        if (method.isAnnotationPresent(annotation)) {
            if (method.getParameterTypes().length != 1) {
                throw new XOException("Life cycle method '" + method.toGenericString() + "' annotated with '" + annotation.getName()
                    + "' must declare exactly one parameter but declares " + method.getParameterTypes().length + ".");
            }
            Set<Method> listenerMethods = methods.computeIfAbsent(listener, k -> new HashSet<>());
            listenerMethods.add(method);
        }
    }

    /**
     * Invokes all registered lifecycle methods on a given instance.
     *
     * @param methods
     *     The registered methods.
     * @param instance
     *     The instance.
     * @param <T>
     *     The instance type.
     */
    private <T> void invoke(Map<Object, Set<Method>> methods, T instance) {
        for (Map.Entry<Object, Set<Method>> entry : methods.entrySet()) {
            Object listener = entry.getKey();
            Set<Method> listenerMethods = entry.getValue();
            if (listenerMethods != null) {
                for (Method method : listenerMethods) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    if (parameterType.isAssignableFrom(instance.getClass())) {
                        try {
                            method.invoke(listener, instance);
                        } catch (IllegalAccessException e) {
                            throw new XOException("Cannot access instance listener method " + method.toGenericString(), e);
                        } catch (InvocationTargetException e) {
                            throw new XOException("Cannot invoke instance listener method " + method.toGenericString(), e);
                        }
                    }
                }
            }
        }
    }
}
