package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.neo4j.api.Neo4jDatastore;
import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import org.neo4j.graphdb.Label;

/**
 * Abstract base class for Neo4j datastores.
 * 
 * @param <DS>
 *            The datastore session type.
 */
public abstract class AbstractNeo4jDatastore<DS extends Neo4jDatastoreSession> implements Neo4jDatastore<DS> {

    private final Neo4jMetadataFactory metadataFactory = new Neo4jMetadataFactory();

    @Override
    public DatastoreMetadataFactory<NodeMetadata, Label, RelationshipMetadata, RelationshipType> getMetadataFactory() {
        return metadataFactory;
    }

}
