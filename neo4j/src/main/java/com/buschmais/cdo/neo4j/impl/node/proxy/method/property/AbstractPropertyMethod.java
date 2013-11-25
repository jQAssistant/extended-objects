package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.AbstractMethodMetadata;
import org.neo4j.graphdb.Node;

public abstract class AbstractPropertyMethod<M extends AbstractMethodMetadata> implements NodeProxyMethod {

    private M metadata;
    private InstanceManager<Long,Node> instanceManager;

    protected AbstractPropertyMethod(M metadata, InstanceManager<Long,Node> instanceManager) {
        this.metadata = metadata;
        this.instanceManager = instanceManager;
    }

    protected M getMetadata() {
        return metadata;
    }

    protected InstanceManager<Long,Node> getInstanceManager() {
        return instanceManager;
    }
}
