package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.spi.reflection.TypeMethod;

public class ImplementedByMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<TypeMethod, DatastoreMetadata> {

    private Class<? extends ProxyMethod<?>> proxyMethodType;

    public ImplementedByMethodMetadata(TypeMethod typeMethod, Class<? extends ProxyMethod<?>> proxyMethodType, DatastoreMetadata datastoreMetadata) {
        super(typeMethod, datastoreMetadata);
        this.proxyMethodType = proxyMethodType;
    }

    public Class<? extends ProxyMethod<?>> getProxyMethodType() {
        return proxyMethodType;
    }
}
