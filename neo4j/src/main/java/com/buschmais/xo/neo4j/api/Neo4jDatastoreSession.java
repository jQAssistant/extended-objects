package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.spi.datastore.DatastoreSession;

/**
 * Defines the Neo4j specific {@link DatastoreSession} interface.
 *
 * @param <GDS>
 *            The type of {@link GraphDatabaseService} which is used by the
 *            concrete implementation.
 */
public interface Neo4jDatastoreSession<GDS extends GraphDatabaseService>
        extends DatastoreSession<Long, Neo4jNode, NodeMetadata, Neo4jLabel, Long, Neo4jRelationship, RelationshipMetadata, RelationshipType, PropertyMetadata> {

    GDS getGraphDatabaseService();

    Object convertValue(Object value);

    Object convertParameter(Object value);
}
