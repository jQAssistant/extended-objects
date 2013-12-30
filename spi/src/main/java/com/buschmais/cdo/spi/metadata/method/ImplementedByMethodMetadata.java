package com.buschmais.cdo.spi.metadata.method;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

public class ImplementedByMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<AnnotatedMethod, DatastoreMetadata> {

    private Class<? extends ProxyMethod<?>> proxyMethodType;

    public ImplementedByMethodMetadata(AnnotatedMethod annotatedMethod, Class<? extends ProxyMethod<?>> proxyMethodType, DatastoreMetadata datastoreMetadata) {
        super(annotatedMethod, datastoreMetadata);
        this.proxyMethodType = proxyMethodType;
    }

    public Class<? extends ProxyMethod<?>> getProxyMethodType() {
        return proxyMethodType;
    }
}
