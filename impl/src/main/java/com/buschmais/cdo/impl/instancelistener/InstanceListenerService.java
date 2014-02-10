package com.buschmais.cdo.impl.instancelistener;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.annotation.*;
import com.buschmais.cdo.impl.reflection.ClassHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class InstanceListenerService {

    private static Map<Object, Method> postCreateMethods;
    private static Map<Object, Method> preUpdateMethods;
    private static Map<Object, Method> postUpdateMethods;
    private static Map<Object, Method> preDeleteMethods;
    private static Map<Object, Method> postDeleteMethods;
    private static Map<Object, Method> postLoadMethods;

    public InstanceListenerService(List<Class<?>> instanceListenerTypes) {
        postCreateMethods = new IdentityHashMap<>();
        preUpdateMethods = new IdentityHashMap<>();
        postUpdateMethods = new IdentityHashMap<>();
        preDeleteMethods = new IdentityHashMap<>();
        postDeleteMethods = new IdentityHashMap<>();
        postLoadMethods = new IdentityHashMap<>();
        for (Class<?> listenerType : instanceListenerTypes) {
            Object instanceListener = ClassHelper.newInstance(listenerType);
            for (Method method : listenerType.getMethods()) {
                if (method.isAnnotationPresent(PostCreate.class)) {
                    postCreateMethods.put(instanceListener, method);
                } else if (method.isAnnotationPresent(PreUpdate.class)) {
                    preUpdateMethods.put(instanceListener, method);
                } else if (method.isAnnotationPresent(PostUpdate.class)) {
                    postUpdateMethods.put(instanceListener, method);
                } else if (method.isAnnotationPresent(PreDelete.class)) {
                    preDeleteMethods.put(instanceListener, method);
                } else if (method.isAnnotationPresent(PostDelete.class)) {
                    postDeleteMethods.put(instanceListener, method);
                } else if (method.isAnnotationPresent(PostLoad.class)) {
                    postLoadMethods.put(instanceListener, method);
                }
            }
        }
    }

    public <T> void postCreate(T instance) {
        invoke(postCreateMethods, instance);
    }

    public <T> void preUpdate(T instance) {
        invoke(preUpdateMethods, instance);
    }

    public <T> void postUpdate(T instance) {
        invoke(postUpdateMethods, instance);
    }

    public <T> void preDelete(T instance) {
        invoke(preDeleteMethods, instance);
    }

    public <T> void postDelete(T instance) {
        invoke(postDeleteMethods, instance);
    }

    public <T> void postLoad(T instance) {
        invoke(postLoadMethods, instance);
    }

    private <T> void invoke(Map<Object, Method> methods, T instance) {
        for (Map.Entry<Object, Method> entry : methods.entrySet()) {
            Object listener = entry.getKey();
            Method method = entry.getValue();
            try {
                method.invoke(listener, new Object[]{instance});
            } catch (IllegalAccessException e) {
                throw new CdoException("Cannot access instance listener method " + method.toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new CdoException("Cannot invoke instance listener method " + method.toGenericString(), e);
            }
        }
    }
}
