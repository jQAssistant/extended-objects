package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.metadata.AbstractMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;

public abstract class AbstractPropertyMethod<M extends AbstractMethodMetadata> implements NodeProxyMethod {

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
