package com.buschmais.cdo.neo4j.impl.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.metadata.AbstractMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.proxy.method.ProxyMethod;

public abstract class AbstractPropertyMethod<M extends AbstractMethodMetadata> implements ProxyMethod {

    private M metadata;
    private InstanceManager instanceManager;

    protected AbstractPropertyMethod(M metadata, InstanceManager instanceManager) {
        this.metadata = metadata;
        this.instanceManager = instanceManager;
    }

    protected M getMetadata() {
        return metadata;
    }

    protected InstanceManager getInstanceManager() {
        return instanceManager;
    }
}
