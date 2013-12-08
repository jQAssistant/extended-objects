package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;

public class ReferencePropertySetMethod<Entity> extends AbstractPropertyMethod<Entity, ReferencePropertyMethodMetadata> {

    public ReferencePropertySetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        Entity target = value != null ? getInstanceManager().getEntity(value) : null;
        getPropertyManager().createSingleRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), target);
        return null;
    }
}
