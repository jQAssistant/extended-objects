package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.xo.api.metadata.method.EntityReferencePropertyMethodMetadata;

public class EntityReferencePropertySetMethod<Entity, Relation>
        extends AbstractPropertyMethod<Entity, EntityPropertyManager<Entity, Relation, ?>, EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertySetMethod(EntityPropertyManager<Entity, Relation, ?> propertyManager, EntityReferencePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        getPropertyManager().createEntityReference(entity, getMetadata(), value);
        return null;
    }
}
