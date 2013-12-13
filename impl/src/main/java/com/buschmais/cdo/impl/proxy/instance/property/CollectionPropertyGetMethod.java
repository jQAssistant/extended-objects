package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.proxy.collection.CollectionProxy;
import com.buschmais.cdo.impl.proxy.collection.ListProxy;
import com.buschmais.cdo.impl.proxy.collection.SetProxy;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.interceptor.TransactionInterceptor;
import com.buschmais.cdo.spi.metadata.CollectionPropertyMethodMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.buschmais.cdo.api.CdoManagerFactory.TransactionAttribute;

public class CollectionPropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity, CollectionPropertyMethodMetadata> {

    private InterceptorFactory interceptorFactory;

    public CollectionPropertyGetMethod(CollectionPropertyMethodMetadata<?> metadata, InstanceManager instanceManager, PropertyManager propertyManager, InterceptorFactory interceptorFactory) {
        super(metadata, instanceManager, propertyManager);
        this.interceptorFactory = interceptorFactory;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        CollectionPropertyMethodMetadata<?> collectionPropertyMetadata = getMetadata();
        CollectionProxy<?, Entity> collectionProxy = new CollectionProxy<>(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), getInstanceManager(), getPropertyManager(), interceptorFactory);
        Collection<?> collection;
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            collection = new SetProxy<>(collectionProxy);
        } else if (List.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            collection = new ListProxy<>(collectionProxy);
        } else if (Collection.class.isAssignableFrom(collectionPropertyMetadata.getBeanMethod().getType())) {
            collection = collectionProxy;
        } else {
            throw new CdoException("Unsupported collection type " + collectionPropertyMetadata.getBeanMethod().getType());
        }
        Collection<?> result = interceptorFactory.addInterceptor(collection);
        return result;
    }
}
