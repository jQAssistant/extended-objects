package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.collection.CollectionProxy;
import com.buschmais.cdo.impl.proxy.collection.ListProxy;
import com.buschmais.cdo.impl.proxy.collection.SetProxy;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.spi.metadata.method.CollectionPropertyMethodMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CollectionPropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, CollectionPropertyMethodMetadata> {

    public CollectionPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, CollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, metadata);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        CollectionPropertyMethodMetadata<?> collectionPropertyMetadata = getMetadata();
        CollectionProxy<?, Entity> collectionProxy = new CollectionProxy<>(getSessionContext(), entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection());
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
