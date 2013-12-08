package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;

public class ImplementedByMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<BeanMethod, DatastoreMetadata> {

    private Class<? extends NodeProxyMethod> proxyMethodType;

    public ImplementedByMethodMetadata(BeanMethod beanMethod, Class<? extends NodeProxyMethod> proxyMethodType) {
        super(beanMethod);
        this.proxyMethodType = proxyMethodType;
    }

    public Class<? extends NodeProxyMethod> getProxyMethodType() {
        return proxyMethodType;
    }
}
