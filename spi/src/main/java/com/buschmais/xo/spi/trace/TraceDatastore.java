package com.buschmais.xo.spi.trace;

import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

import java.util.Collection;

/**
 * {@link Datastore} implementation allowing tracing on delegates.
 */
public class TraceDatastore<DatastoreSession extends com.buschmais.xo.spi.datastore.DatastoreSession, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements Datastore<TraceDatastoreSession, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> {

    private Datastore<DatastoreSession, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegate;

    public TraceDatastore(Datastore<DatastoreSession, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void init(Collection registeredMetadata) {
        delegate.init(registeredMetadata);
    }

    @Override
    public DatastoreMetadataFactory<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> getMetadataFactory() {
        return delegate.getMetadataFactory();
    }

    @Override
    public TraceDatastoreSession createSession() {
        DatastoreSession delegateSession = delegate.createSession();
        return new TraceDatastoreSession(delegateSession);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
