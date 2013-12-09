package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.CollectionPropertyMethodMetadata;

import java.util.Collection;

public class CollectionPropertySetMethod<Entity> extends AbstractPropertyMethod<Entity, CollectionPropertyMethodMetadata> {

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
