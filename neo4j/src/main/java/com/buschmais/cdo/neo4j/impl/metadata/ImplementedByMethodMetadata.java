package com.buschmais.cdo.neo4j.impl.metadata;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

public class ImplementedByMethodMetadata extends AbstractMethodMetadata<BeanMethod> {

    private Class<? extends ProxyMethod> proxyMethodType;

    public ImplementedByMethodMetadata(BeanMethod beanMethod, Class<? extends ProxyMethod> proxyMethodType) {
        super(beanMethod);
        this.proxyMethodType = proxyMethodType;
    }

    public Class<? extends ProxyMethod> getProxyMethodType() {
        return proxyMethodType;
    }
}
