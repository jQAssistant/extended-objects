package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;
import com.buschmais.xo.api.proxy.ProxyMethod;

public class ImplementedByMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<AnnotatedMethod, DatastoreMetadata> {

    private final Class<? extends ProxyMethod<?>> proxyMethodType;

    public ImplementedByMethodMetadata(AnnotatedMethod annotatedMethod, Class<? extends ProxyMethod<?>> proxyMethodType, DatastoreMetadata datastoreMetadata) {
        super(annotatedMethod, datastoreMetadata);
        this.proxyMethodType = proxyMethodType;
    }

    public Class<? extends ProxyMethod<?>> getProxyMethodType() {
        return proxyMethodType;
    }
}
