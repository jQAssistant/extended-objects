package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.spi.proxy.ProxyMethod;

public class ImplementedByMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<BeanMethod, DatastoreMetadata> {

    private Class<? extends ProxyMethod<?>> proxyMethodType;

    public ImplementedByMethodMetadata(BeanMethod beanMethod, Class<? extends NodeProxyMethod> proxyMethodType, DatastoreMetadata datastoreMetadata) {
        super(beanMethod, datastoreMetadata);
        this.proxyMethodType = proxyMethodType;
    }

    public Class<? extends ProxyMethod<?>> getProxyMethodType() {
        return proxyMethodType;
    }
}
