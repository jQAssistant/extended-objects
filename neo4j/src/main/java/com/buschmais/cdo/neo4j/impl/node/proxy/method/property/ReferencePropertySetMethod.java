package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;

public class ReferencePropertySetMethod extends AbstractPropertyMethod<ReferencePropertyMethodMetadata> {

    public ReferencePropertySetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        Object value = args[0];
        Node target = value != null ? getInstanceManager().getEntity(value) : null;
        getPropertyManager().createSingleRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), target);
        return null;
    }
}
