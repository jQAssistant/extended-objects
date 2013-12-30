package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public abstract class AbstractNeo4jDatastore<DS extends AbstractNeo4jDatastoreSession> implements Datastore<DS, NodeMetadata, Label, RelationshipMetadata, RelationshipType> {

    private Neo4jMetadataFactory metadataFactory = new Neo4jMetadataFactory();

    @Override
    public DatastoreMetadataFactory<NodeMetadata, Label, RelationshipMetadata, RelationshipType> getMetadataFactory() {
        return metadataFactory;
    }

}
