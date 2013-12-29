package com.buschmais.cdo.impl.proxy.instance;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.spi.metadata.UnsupportedOperationMethodMetadata;

import java.lang.reflect.Method;

public class UnsupportedOperationMethod<Entity> implements ProxyMethod<Entity> {

    private UnsupportedOperationMethodMetadata methodMetadata;

    public UnsupportedOperationMethod(UnsupportedOperationMethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Method method = methodMetadata.getAnnotatedMethod().getAnnotatedElement();
        throw new UnsupportedOperationException("Method '" + method.getName() + "' declared in '" + method.getDeclaringClass().getName() + "' is not mapped to an implementation.");
    }
}
