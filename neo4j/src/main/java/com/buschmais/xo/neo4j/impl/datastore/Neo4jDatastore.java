package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.impl.datastore.metadata.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import org.neo4j.graphdb.Label;

/**
 * Base interface for Neoj datastores.
 */
public interface Neo4jDatastore<DS extends Neo4jDatastoreSession> extends Datastore<DS, NodeMetadata, Label, RelationshipMetadata, Neo4jRelationshipType> {
}
