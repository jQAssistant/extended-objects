package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.impl.datastore.metadata.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import org.neo4j.graphdb.Label;

public abstract class AbstractNeo4jDatastore<DS extends Neo4jDatastoreSession> implements Neo4jDatastore<DS> {

    private final Neo4jMetadataFactory metadataFactory = new Neo4jMetadataFactory();

    @Override
    public DatastoreMetadataFactory<NodeMetadata, Label, RelationshipMetadata, Neo4jRelationshipType> getMetadataFactory() {
        return metadataFactory;
    }

}
