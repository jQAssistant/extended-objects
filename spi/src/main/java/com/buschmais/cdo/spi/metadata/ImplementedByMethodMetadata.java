package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.spi.reflection.BeanMethod;

public class ImplementedByMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<BeanMethod, DatastoreMetadata> {

    private Class<? extends ProxyMethod<?>> proxyMethodType;

    public ImplementedByMethodMetadata(BeanMethod beanMethod, Class<? extends ProxyMethod<?>> proxyMethodType, DatastoreMetadata datastoreMetadata) {
        super(beanMethod, datastoreMetadata);
        this.proxyMethodType = proxyMethodType;
    }

    public Class<? extends ProxyMethod<?>> getProxyMethodType() {
        return proxyMethodType;
    }
}
