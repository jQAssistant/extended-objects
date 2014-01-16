package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.CollectionPropertyMethodMetadata;

import java.util.Collection;

public class CollectionPropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, CollectionPropertyMethodMetadata> {

    public CollectionPropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, CollectionPropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        PropertyManager<?, Entity, ?, Relation> propertyManager = getSessionContext().getPropertyManager();
        propertyManager.removeRelations(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection());
        Collection<?> collection = (Collection<?>) value;
        for (Object o : collection) {
            Entity target = getSessionContext().getEntityInstanceManager().getDatastoreType(o);
            propertyManager.createRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), target);
        }
        return null;
    }
}
