package com.buschmais.xo.neo4j.embedded.api;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;

/**
 * Base interface for Neoj datastores.
 */
public interface Neo4jDatastore<L extends Neo4jLabel, R extends Neo4jRelationshipType, DS extends Neo4jDatastoreSession>
        extends Datastore<DS, NodeMetadata<L>, L, RelationshipMetadata<R>, R> {
}
