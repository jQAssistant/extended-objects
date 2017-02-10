package com.buschmais.xo.neo4j.remote;

import com.buschmais.xo.neo4j.remote.impl.RemoteDatastore;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;

public class Neo4jRemoteStoreProvider
        implements XODatastoreProvider<NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> {

    @Override
    public Datastore<?, NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> createDatastore(
            XOUnit xoUnit) {
        return new RemoteDatastore(xoUnit);
    }
}
