package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.AbstractMethodMetadata;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public abstract class AbstractPropertyMethod<M extends AbstractMethodMetadata> implements NodeProxyMethod {

    private M metadata;
    private InstanceManager<Long, Node> instanceManager;
    private PropertyManager<Long, Node, Long, Relationship> propertyManager;

    protected AbstractPropertyMethod(M metadata, InstanceManager<Long, Node> instanceManager, PropertyManager<Long, Node, Long, Relationship> propertyManager) {
        this.metadata = metadata;
        this.instanceManager = instanceManager;
        this.propertyManager = propertyManager;
    }

    protected M getMetadata() {
        return metadata;
    }

    protected InstanceManager<Long, Node> getInstanceManager() {
        return instanceManager;
    }

    public PropertyManager<Long, Node, Long, Relationship> getPropertyManager() {
        return propertyManager;
    }
}
