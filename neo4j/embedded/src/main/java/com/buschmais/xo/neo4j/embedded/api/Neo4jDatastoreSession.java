package com.buschmais.xo.neo4j.embedded.api;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;

/**
 * Defines the Neo4j specific {@link DatastoreSession} interface.
 *
 * @param <GDS>
 *            The type of {@link GraphDatabaseService} which is used by the
 *            concrete implementation.
 */
public interface Neo4jDatastoreSession<GDS extends GraphDatabaseService> extends
        DatastoreSession<Long, EmbeddedNode, NodeMetadata<EmbeddedLabel>, EmbeddedLabel, Long, EmbeddedRelationship, RelationshipMetadata<EmbeddedRelationshipType>, EmbeddedRelationshipType, PropertyMetadata> {

    GDS getGraphDatabaseService();

    Object convertValue(Object value);

    Object convertParameter(Object value);
}
