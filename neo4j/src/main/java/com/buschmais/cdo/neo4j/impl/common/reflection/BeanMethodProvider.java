package com.buschmais.cdo.neo4j.impl.common.reflection;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeanMethodProvider {

    public Collection<BeanMethod> getMethods(Class<?> type) {
        List<BeanMethod> beanMethods = new ArrayList<>();
        for (Method method : type.getDeclaredMethods()) {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (methodName.startsWith("get") && parameterTypes.length == 0 && !void.class.equals(returnType)) {
                beanMethods.add(new BeanPropertyMethod(method, BeanPropertyMethod.MethodType.GETTER, StringUtils.uncapitalize(methodName.substring(3)), returnType));
            } else if (methodName.startsWith("is") && parameterTypes.length == 0 && !void.class.equals(returnType)) {
                beanMethods.add(new BeanPropertyMethod(method, BeanPropertyMethod.MethodType.GETTER, StringUtils.uncapitalize(methodName.substring(2)), returnType));
            } else if (methodName.startsWith("set") && parameterTypes.length == 1 && void.class.equals(returnType) && methodName.startsWith("set")) {
                beanMethods.add(new BeanPropertyMethod(method, BeanPropertyMethod.MethodType.SETTER, StringUtils.uncapitalize(methodName.substring(3)), parameterTypes[0]));
            } else {
                beanMethods.add(new BeanMethod(method));
            }
        }
        return beanMethods;
    }

}
