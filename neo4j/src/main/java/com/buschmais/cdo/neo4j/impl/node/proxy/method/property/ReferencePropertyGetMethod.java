package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import org.neo4j.graphdb.Node;

public class ReferencePropertyGetMethod extends AbstractPropertyMethod<ReferencePropertyMethodMetadata> {

    public ReferencePropertyGetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        Node endNode = getPropertyManager().getSingleRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection());
        return endNode != null ? getInstanceManager().getInstance(endNode) : null;
    }
}
