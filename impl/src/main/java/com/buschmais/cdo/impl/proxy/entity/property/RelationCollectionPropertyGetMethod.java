package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.collection.ListProxy;
import com.buschmais.cdo.impl.proxy.collection.RelationCollectionProxy;
import com.buschmais.cdo.impl.proxy.collection.SetProxy;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.RelationCollectionPropertyMethodMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RelationCollectionPropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Entity, Relation, RelationCollectionPropertyMethodMetadata> {

    public RelationCollectionPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, RelationCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Entity, Entity, Relation> getPropertyManager() {
        return getSessionContext().getEntityPropertyManager();
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        RelationCollectionPropertyMethodMetadata<?> collectionPropertyMetadata = getMetadata();
        RelationCollectionProxy<?, Entity, Relation> collectionProxy = new RelationCollectionProxy<>(getSessionContext(), entity, getMetadata());
        Collection<?> collection;
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getAnnotatedMethod().getType())) {
            collection = new SetProxy<>(collectionProxy);
        } else if (List.class.isAssignableFrom(collectionPropertyMetadata.getAnnotatedMethod().getType())) {
            collection = new ListProxy<>(collectionProxy);
        } else if (Collection.class.isAssignableFrom(collectionPropertyMetadata.getAnnotatedMethod().getType())) {
            collection = collectionProxy;
        } else {
            throw new CdoException("Unsupported collection type " + collectionPropertyMetadata.getAnnotatedMethod().getType());
        }
        Collection<?> result = getSessionContext().getInterceptorFactory().addInterceptor(collection);
        return result;
    }

}
