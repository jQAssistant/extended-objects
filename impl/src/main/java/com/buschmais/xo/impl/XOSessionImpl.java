package com.buschmais.xo.impl;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.impl.transaction.TransactionalResultIterator;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.metadata.type.RepositoryTypeMetadata;
import com.buschmais.xo.spi.session.InstanceManager;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Implementation of the {@link XOSession} interface.
 */
public class XOSessionImpl<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata>
        implements XOSession<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> {

    private SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> sessionContext;

    /**
     * Constructor.
     * 
     * @param sessionContext
     *            The session context.
     */
    public XOSessionImpl(
            SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public <T, D> D toDatastore(T value) {
        return (D) value;
    }

    @Override
    public <D, T> T fromDatastore(D value) {
        InstanceManager<?, D> instanceManager = getInstanceManager(value);
        if (instanceManager != null) {
            return instanceManager.readInstance(value);
        }
        return (T) value;
    }

    @Override
    public <T> EntityTypeMetadata<EntityMetadata> getEntityMetadata(Class<T> type) {
        return sessionContext.getMetadataProvider().getEntityMetadata(type);
    }

    @Override
    public <T> RelationTypeMetadata<RelationMetadata> getRelationMetadata(Class<T> type) {
        return sessionContext.getMetadataProvider().getRelationMetadata(type);
    }

    @Override
    public <R> RepositoryTypeMetadata getRepositoryMetadata(Class<R> type) {
        return sessionContext.getMetadataProvider().getRepositoryMetadata(type);
    }

    @Override
    public <D> InstanceManager<?, D> getInstanceManager(D datastoreType) {
        if (sessionContext.getEntityInstanceManager().isDatastoreType(datastoreType)) {
            return (InstanceManager<?, D>) sessionContext.getEntityInstanceManager();
        } else if (sessionContext.getRelationInstanceManager().isDatastoreType(datastoreType)) {
            return (InstanceManager<?, D>) sessionContext.getRelationInstanceManager();
        }
        return null;
    }

    @Override
    public <D, T> ResultIterable<T> toResult(ResultIterator<D> iterator) {
        ResultIterator<T> resultIterator = new ResultIterator<T>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                Object datastoreValue = iterator.next();
                return fromDatastore(datastoreValue);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove instance.");
            }

            @Override
            public void close() {
                iterator.close();
            }
        };
        XOTransaction xoTransaction = sessionContext.getXOTransaction();
        final ResultIterator<T> transactionalIterator = xoTransaction != null ? new TransactionalResultIterator<>(resultIterator, xoTransaction)
                : resultIterator;
        return sessionContext.getInterceptorFactory().addInterceptor(new AbstractResultIterable<T>() {
            @Override
            public ResultIterator<T> iterator() {
                return transactionalIterator;
            }
        }, ResultIterable.class);
    }

    @Override
    public void flush() {
        sessionContext.getCacheSynchronizationService().flush();
    }

    @Override
    public void clear() {
        sessionContext.getCacheSynchronizationService().clear();
    }
}
