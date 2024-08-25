package com.buschmais.xo.impl.proxy.common;

import java.lang.reflect.Method;

import com.buschmais.xo.api.metadata.method.UnsupportedOperationMethodMetadata;
import com.buschmais.xo.api.proxy.ProxyMethod;

public class UnsupportedOperationMethod<DatastoreType> implements ProxyMethod<DatastoreType> {

    private final UnsupportedOperationMethodMetadata<?> methodMetadata;

    public UnsupportedOperationMethod(UnsupportedOperationMethodMetadata<?> methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Method method = methodMetadata.getAnnotatedMethod()
            .getAnnotatedElement();
        throw new UnsupportedOperationException("Method '" + method.getName() + "' declared in '" + method.getDeclaringClass()
            .getName() + "' is not mapped to an implementation.");
    }
}
