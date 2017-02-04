package com.buschmais.xo.neo4j.embedded.api;

import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.spi.datastore.Datastore;

/**
 * Base interface for Neoj datastores.
 */
public interface Neo4jDatastore<DS extends Neo4jDatastoreSession> extends Datastore<DS, NodeMetadata, EmbeddedLabel, RelationshipMetadata, EmbeddedRelationshipType> {
}
