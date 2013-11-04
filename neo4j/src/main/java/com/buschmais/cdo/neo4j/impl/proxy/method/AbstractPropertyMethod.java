package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.metadata.AbstractPropertyMetadata;

public abstract class AbstractPropertyMethod<M extends AbstractPropertyMetadata> implements ProxyMethod {

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
