package com.buschmais.xo.spi.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;
import com.buschmais.xo.api.metadata.reflection.GetPropertyMethod;
import com.buschmais.xo.api.metadata.reflection.SetPropertyMethod;
import com.buschmais.xo.api.metadata.reflection.UserMethod;

import com.google.common.base.CaseFormat;

/**
 * Reflection based utility for analyzing classes.
 */
public final class BeanMethodProvider {

    enum PropertyMethod {

        SET {
            @Override
            boolean matches(Class<?>[] parameterTypes, Class<?> returnType) {
                return parameterTypes.length == 1 && void.class.equals(returnType);
            }
        },
        GET {
            @Override
            boolean matches(Class<?>[] parameterTypes, Class<?> returnType) {
                return parameterTypes.length == 0 && !void.class.equals(returnType);
            }
        },
        IS {
            @Override
            boolean matches(Class<?>[] parameterTypes, Class<?> returnType) {
                return parameterTypes.length == 0 && !void.class.equals(returnType);
            }
        };

        abstract boolean matches(Class<?>[] parameterTypes, Class<?> returnType);

        public boolean matches(Method method) {
            return method.getName()
                .startsWith(name().toLowerCase()) && matches(method.getParameterTypes(), method.getReturnType());
        }

        String getName(Method method) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, method.getName()
                .substring(name().length()));
        }
    }

    private final Class<?> type;
    private final Set<Method> methods = new HashSet<>();
    private final Map<String, Method> getters = new HashMap<>();
    private final Map<String, Method> setters = new HashMap<>();
    private final Map<String, Class<?>> types = new HashMap<>();
    private final Map<String, Type> genericTypes = new HashMap<>();

    /**
     * Private constructor.
     */
    private BeanMethodProvider(Class<?> type) {
        this.type = type;
    }

    /**
     * Creates a new instance.
     *
     * @return The instance.
     */
    public static BeanMethodProvider newInstance(Class<?> type) {
        return new BeanMethodProvider(type);
    }

    /**
     * Return the methods of the type.
     *
     * @return The methods.
     */
    public Collection<AnnotatedMethod> getMethods() {
        for (Method method : type.getDeclaredMethods()) {
            // only consider methods which have been explicitly declared in the
            // interface
            if (!method.isSynthetic()) {
                Class<?> returnType = method.getReturnType();
                Type genericReturnType = method.getGenericReturnType();
                Class<?>[] parameterTypes = method.getParameterTypes();
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                if (PropertyMethod.GET.matches(method)) {
                    String name = PropertyMethod.GET.getName(method);
                    getters.put(name, method);
                    addType(type, name, returnType, genericReturnType);
                } else if (PropertyMethod.IS.matches(method)) {
                    String name = PropertyMethod.IS.getName(method);
                    getters.put(name, method);
                    addType(type, name, returnType, genericReturnType);
                } else if (PropertyMethod.SET.matches(method)) {
                    String name = PropertyMethod.SET.getName(method);
                    setters.put(name, method);
                    addType(type, name, parameterTypes[0], genericParameterTypes[0]);
                } else {
                    methods.add(method);
                }
            }
        }
        List<AnnotatedMethod> typeMethods = new ArrayList<>();
        Map<String, GetPropertyMethod> getPropertyMethods = new HashMap<>();
        for (Map.Entry<String, Method> methodEntry : getters.entrySet()) {
            String name = methodEntry.getKey();
            Method getter = methodEntry.getValue();
            Class<?> propertyType = types.get(name);
            Type genericType = genericTypes.get(name);
            GetPropertyMethod getPropertyMethod = new GetPropertyMethod(getter, name, propertyType, genericType);
            typeMethods.add(getPropertyMethod);
            getPropertyMethods.put(name, getPropertyMethod);
        }
        for (Map.Entry<String, Method> methodEntry : setters.entrySet()) {
            String name = methodEntry.getKey();
            Method setter = methodEntry.getValue();
            GetPropertyMethod getPropertyMethod = getPropertyMethods.get(name);
            Class<?> propertyType = types.get(name);
            Type genericType = genericTypes.get(name);
            SetPropertyMethod setPropertyMethod = new SetPropertyMethod(setter, getPropertyMethod, name, propertyType, genericType);
            typeMethods.add(setPropertyMethod);
        }
        for (Method method : methods) {
            typeMethods.add(new UserMethod(method));
        }
        return typeMethods;
    }

    private void addType(Class<?> declaringType, String name, Class<?> type, Type genericType) {
        Class<?> existingType = types.put(name, type);
        if (existingType != null && !existingType.equals(type)) {
            throw new XOException("Get and set methods for property '" + name + "' of type '" + declaringType.getName() + "' do not declare the same type: "
                + existingType.getName() + " <> " + type.getName());
        }
        Type existingGenericType = genericTypes.put(name, genericType);
        if (existingGenericType != null && !existingGenericType.equals(genericType)) {
            throw new XOException(
                "Get and set methods for property '" + name + "' of type '" + declaringType.getName() + "' do not declare the same generic type: "
                    + existingGenericType + " <> " + type.getName());
        }
    }
}
