package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;

public class ReferencePropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity, ReferencePropertyMethodMetadata> {

    public ReferencePropertyGetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Entity target = getPropertyManager().getSingleRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection());
        return target != null ? getInstanceManager().getInstance(target) : null;
    }
}
