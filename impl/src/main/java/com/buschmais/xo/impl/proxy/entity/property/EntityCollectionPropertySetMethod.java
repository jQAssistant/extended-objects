package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.xo.spi.metadata.method.EntityCollectionPropertyMethodMetadata;

import java.util.Collection;

public class EntityCollectionPropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, EntityPropertyManager<Entity, Relation>, EntityCollectionPropertyMethodMetadata> {

    public EntityCollectionPropertySetMethod(EntityPropertyManager<Entity, Relation> propertyManager, EntityCollectionPropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        EntityPropertyManager<Entity, Relation> propertyManager = getPropertyManager();
        propertyManager.removeEntityReferences(entity, getMetadata());
        Collection<?> collection = (Collection<?>) args[0];
        for (Object o : collection) {
            propertyManager.createEntityReference(entity, getMetadata(), o);
        }
        return null;
    }
}
