package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.impl.EntityPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.collection.ListProxy;
import com.buschmais.cdo.impl.proxy.collection.RelationCollectionProxy;
import com.buschmais.cdo.impl.proxy.collection.SetProxy;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.RelationCollectionPropertyMethodMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RelationCollectionPropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, EntityPropertyManager<Entity, Relation>, RelationCollectionPropertyMethodMetadata> {

    private SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;

    public RelationCollectionPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, RelationCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext.getEntityPropertyManager(), metadata);
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        RelationCollectionPropertyMethodMetadata<?> collectionPropertyMetadata = getMetadata();
        RelationCollectionProxy<?, Entity, Relation> collectionProxy = new RelationCollectionProxy<>(sessionContext, entity, getMetadata());
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
        Collection<?> result = sessionContext.getInterceptorFactory().addInterceptor(collection);
        return result;
    }

}
