package com.buschmais.xo.neo4j.spi;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;

/**
 * Defines the Neo4j specific {@link DatastoreSession} interface.
 */
public interface Neo4jDatastoreSession<N extends Neo4jNode, L extends Neo4jLabel, R extends Neo4jRelationship, T extends Neo4jRelationshipType>
        extends DatastoreSession<Long, N, NodeMetadata<L>, L, Long, R, RelationshipMetadata<T>, T, PropertyMetadata> {

    Object convertValue(Object value);

    Object convertParameter(Object value);
}
