package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;

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
