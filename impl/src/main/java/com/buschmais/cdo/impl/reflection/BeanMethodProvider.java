package com.buschmais.cdo.impl.reflection;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.GetPropertyMethod;
import com.buschmais.cdo.spi.reflection.SetPropertyMethod;
import com.buschmais.cdo.spi.reflection.UserMethod;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

public final class BeanMethodProvider {

    private final Set<Method> methods = new HashSet<>();
    private final Map<String, Method> getters = new HashMap<>();
    private final Map<String, Method> setters = new HashMap<>();
    private final Map<String, Class<?>> types = new HashMap<>();

    private BeanMethodProvider() {
    }

    public static BeanMethodProvider newInstance() {
        return new BeanMethodProvider();
    }

    public Collection<AnnotatedMethod> getMethods(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (methodName.startsWith("get") && parameterTypes.length == 0 && !void.class.equals(returnType)) {
                String name = StringUtils.uncapitalize(methodName.substring(3));
                getters.put(name, method);
                addType(type, name, returnType);
            } else if (methodName.startsWith("is") && parameterTypes.length == 0 && !void.class.equals(returnType)) {
                String name = StringUtils.uncapitalize(methodName.substring(2));
                getters.put(name, method);
                addType(type, name, returnType);
            } else if (methodName.startsWith("set") && parameterTypes.length == 1 && void.class.equals(returnType) && methodName.startsWith("set")) {
                String name = StringUtils.uncapitalize(methodName.substring(3));
                setters.put(name, method);
                addType(type, name, parameterTypes[0]);
            } else {
                methods.add(method);
            }
        }
        List<AnnotatedMethod> typeMethods = new ArrayList<>();
        Map<String, GetPropertyMethod> getPropertyMethods = new HashMap<>();
        for (Map.Entry<String, Method> methodEntry : getters.entrySet()) {
            String name = methodEntry.getKey();
            Method getter = methodEntry.getValue();
            Class<?> propertyType = types.get(name);
            GetPropertyMethod getPropertyMethod = new GetPropertyMethod(getter, name, propertyType);
            typeMethods.add(getPropertyMethod);
            getPropertyMethods.put(name, getPropertyMethod);
        }
        for (Map.Entry<String, Method> methodEntry : setters.entrySet()) {
            String name = methodEntry.getKey();
            Method setter = methodEntry.getValue();
            GetPropertyMethod getPropertyMethod = getPropertyMethods.get(name);
            Class<?> propertyType = types.get(name);
            SetPropertyMethod setPropertyMethod = new SetPropertyMethod(setter, getPropertyMethod, name, propertyType);
            typeMethods.add(setPropertyMethod);
        }
        for (Method method : methods) {
            typeMethods.add(new UserMethod(method));
        }
        return typeMethods;
    }

    private void addType(Class<?> declaringType, String name, Class<?> type) {
        Class<?> existingType = types.put(name, type);
        if (existingType != null && !existingType.equals(type)) {
            throw new CdoException("Get and set methods for property '" + name + "' of type '" + declaringType.getName() + "' do not declare the same type: " + existingType.getName() + " <> " + type.getName());
        }
    }
}
