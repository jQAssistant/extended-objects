package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.collection.AbstractCollectionProxy;
import com.buschmais.xo.impl.proxy.collection.ListProxy;
import com.buschmais.xo.impl.proxy.collection.SetProxy;
import com.buschmais.xo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.xo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Abstract base implementation for get methods returning collections of datastore types.
 *
 * @param <DatastoreType>   The datastore type.
 * @param <Entity>          The entity type.
 * @param <Relation>        The relation type.
 * @param <PropertyManager> The property manager.
 * @param <M>               The method metadata.
 */
public abstract class AbstractCollectionPropertyGetMethod<DatastoreType, Entity, Relation, PropertyManager extends AbstractPropertyManager<DatastoreType, ?, ?>, M extends AbstractRelationPropertyMethodMetadata<?>> extends AbstractPropertyMethod<DatastoreType, PropertyManager, M> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;

    /**
     * Constructor.
     *
     * @param sessionContext  The session context.
     * @param propertyManager The property manager.
     * @param metadata        The metadata.
     */
    public AbstractCollectionPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, PropertyManager propertyManager, M metadata) {
        super(propertyManager, metadata);
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(DatastoreType entity, Object instance, Object[] args) {
        M collectionPropertyMetadata = getMetadata();
        AbstractCollectionProxy<?, ?, ?, ?> collectionProxy = createCollectionProxy(entity, sessionContext);
        Collection<?> collection;
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getAnnotatedMethod().getType())) {
            collection = new SetProxy<>(collectionProxy);
        } else if (List.class.isAssignableFrom(collectionPropertyMetadata.getAnnotatedMethod().getType())) {
            collection = new ListProxy<>(collectionProxy);
        } else if (Collection.class.isAssignableFrom(collectionPropertyMetadata.getAnnotatedMethod().getType())) {
            collection = collectionProxy;
        } else {
            throw new XOException("Unsupported collection type " + collectionPropertyMetadata.getAnnotatedMethod().getType());
        }
        return sessionContext.getInterceptorFactory().addInterceptor(collection);
    }

    /**
     * Create the collection proxy instance.
     *
     * @param datastoreType  The datastore type (i.e. representing the instance holding the collection property).
     * @param sessionContext The session context.
     * @return The collection proxy.
     */
    protected abstract AbstractCollectionProxy<?, ?, ?, ?> createCollectionProxy(DatastoreType datastoreType, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext);

}
