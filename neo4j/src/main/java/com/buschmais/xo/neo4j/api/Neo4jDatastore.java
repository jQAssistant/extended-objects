package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;

/**
 * Base interface for Neoj datastores.
 */
public interface Neo4jDatastore<DS extends Neo4jDatastoreSession> extends Datastore<DS, NodeMetadata, Neo4jLabel, RelationshipMetadata, RelationshipType> {
}
