package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.method.CollectionPropertyMethodMetadata;

import java.util.Collection;

public class CollectionPropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, CollectionPropertyMethodMetadata> {

    public CollectionPropertySetMethod(CollectionPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        getPropertyManager().removeRelations(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection());
        Collection<?> collection = (Collection<?>) value;
        for (Object o : collection) {
            Entity target = getInstanceManager().getEntity(o);
            getPropertyManager().createRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), target);
        }
        return null;
    }
}
