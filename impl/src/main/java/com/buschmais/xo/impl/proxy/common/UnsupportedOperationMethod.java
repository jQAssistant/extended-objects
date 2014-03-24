package com.buschmais.xo.impl.proxy.common;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.spi.metadata.method.UnsupportedOperationMethodMetadata;

import java.lang.reflect.Method;

public class UnsupportedOperationMethod<DatastoreType> implements ProxyMethod<DatastoreType> {

    private final UnsupportedOperationMethodMetadata<?> methodMetadata;

    public UnsupportedOperationMethod(UnsupportedOperationMethodMetadata<?> methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Method method = methodMetadata.getAnnotatedMethod().getAnnotatedElement();
        throw new UnsupportedOperationException("Method '" + method.getName() + "' declared in '" + method.getDeclaringClass().getName() + "' is not mapped to an implementation.");
    }
}
