package com.buschmais.cdo.impl.reflection;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.spi.reflection.BeanMethod;
import com.buschmais.cdo.spi.reflection.GetPropertyMethod;
import com.buschmais.cdo.spi.reflection.SetPropertyMethod;
import com.buschmais.cdo.spi.reflection.UserMethod;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

public final class BeanMethodProvider {

    private Set<Method> methods = new HashSet<>();
    private Map<String, Method> getters = new HashMap<>();
    private Map<String, Method> setters = new HashMap<>();
    private Map<String, Class<?>> types = new HashMap<>();

    private BeanMethodProvider() {
    }

    public static BeanMethodProvider newInstance() {
        return new BeanMethodProvider();
    }

    public Collection<BeanMethod> getMethods(Class<?> type) {
        for (Method method : type.getDeclaredMethods()) {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (methodName.startsWith("get") && parameterTypes.length == 0 && !void.class.equals(returnType)) {
                String name = StringUtils.uncapitalize(methodName.substring(3));
                getters.put(name, method);
                addType(name, returnType);
            } else if (methodName.startsWith("is") && parameterTypes.length == 0 && !void.class.equals(returnType)) {
                String name = StringUtils.uncapitalize(methodName.substring(2));
                getters.put(name, method);
                addType(name, returnType);
            } else if (methodName.startsWith("set") && parameterTypes.length == 1 && void.class.equals(returnType) && methodName.startsWith("set")) {
                String name = StringUtils.uncapitalize(methodName.substring(3));
                setters.put(name, method);
                addType(name, parameterTypes[0]);
            } else {
                methods.add(method);
            }
        }
        List<BeanMethod> beanMethods = new ArrayList<>();
        Map<String, GetPropertyMethod> getPropertyMethods = new HashMap<>();
        for (Map.Entry<String, Method> methodEntry : getters.entrySet()) {
            String name = methodEntry.getKey();
            Method getter = methodEntry.getValue();
            Class<?> propertyType = types.get(name);
            GetPropertyMethod getPropertyMethod = new GetPropertyMethod(getter, name, propertyType);
            beanMethods.add(getPropertyMethod);
            getPropertyMethods.put(name, getPropertyMethod);
        }
        for (Map.Entry<String, Method> methodEntry : setters.entrySet()) {
            String name = methodEntry.getKey();
            Method setter = methodEntry.getValue();
            GetPropertyMethod getPropertyMethod = getPropertyMethods.get(name);
            Class<?> propertyType = types.get(name);
            SetPropertyMethod setPropertyMethod = new SetPropertyMethod(setter, getPropertyMethod, name, propertyType);
            beanMethods.add(setPropertyMethod);
        }
        for (Method method : methods) {
            beanMethods.add(new UserMethod(method));
        }
        return beanMethods;
    }

    private void addType(String name, Class<?> type) {
        Class<?> existingType = types.put(name, type);
        if (existingType != null && !existingType.equals(type)) {
            throw new CdoException("Get and set methods for property '" + name + "' do not declare the same type.");
        }
    }
}
