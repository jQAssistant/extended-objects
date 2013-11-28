package com.buschmais.cdo.neo4j.impl.common.proxy.method;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.metadata.UnsupportedOperationMethodMetadata;

import java.lang.reflect.Method;

public class UnsupportedOperationMethod<Entity> implements ProxyMethod<Entity> {

    private UnsupportedOperationMethodMetadata methodMetadata;

    public UnsupportedOperationMethod(UnsupportedOperationMethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Method method = methodMetadata.getBeanMethod().getMethod();
        throw new UnsupportedOperationException("Method '" + method.getName() + "' declared in '" + method.getDeclaringClass().getName() + "' is not mapped to an implementation.");
    }
}
