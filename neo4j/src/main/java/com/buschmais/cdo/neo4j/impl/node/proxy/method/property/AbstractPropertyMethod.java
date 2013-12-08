package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;
import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.AbstractMethodMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public abstract class AbstractPropertyMethod<Entity, M extends AbstractMethodMetadata> implements ProxyMethod<Entity> {

    private M metadata;
    private InstanceManager<?, Entity> instanceManager;
    private PropertyManager<?, Entity, ?, ?> propertyManager;

    protected AbstractPropertyMethod(M metadata, InstanceManager<?, Entity> instanceManager, PropertyManager<?, Entity, ?, ?> propertyManager) {
        this.metadata = metadata;
        this.instanceManager = instanceManager;
        this.propertyManager = propertyManager;
    }

    protected M getMetadata() {
        return metadata;
    }

    protected InstanceManager<?, Entity> getInstanceManager() {
        return instanceManager;
    }

    public PropertyManager<?, Entity, ?, ?> getPropertyManager() {
        return propertyManager;
    }
}
