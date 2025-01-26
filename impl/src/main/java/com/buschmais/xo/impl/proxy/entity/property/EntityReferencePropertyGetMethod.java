package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.api.metadata.method.EntityReferencePropertyMethodMetadata;
import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPropertyMethod;

public class EntityReferencePropertyGetMethod<Entity, Relation>
    extends AbstractPropertyMethod<Entity, Relation, Entity, EntityPropertyManager<Entity, Relation, ?>, EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertyGetMethod(EntityPropertyManager<Entity, Relation, ?> propertyManager, EntityReferencePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        EntityReferencePropertyMethodMetadata metadata = getMetadata();
        Object reference = getPropertyManager().getEntityReference(entity, metadata);
        return reference != null && metadata.getElementType()
            .isAssignableFrom(reference.getClass()) ? reference : null;
    }
}
