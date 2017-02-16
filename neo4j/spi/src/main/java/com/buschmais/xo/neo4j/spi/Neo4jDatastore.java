package com.buschmais.xo.neo4j.spi;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreSession;

/**
 * Base interface for Neoj datastores.
 */
public interface Neo4jDatastore<L extends Neo4jLabel, R extends Neo4jRelationshipType, DS extends DatastoreSession>
        extends Datastore<DS, NodeMetadata<L>, L, RelationshipMetadata<R>, R> {
}
