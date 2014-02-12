package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.neo4j.impl.datastore.metadata.Neo4jRelationshipType;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Defines the Neo4j specific {@link DatastoreSession} interface.
 *
 * @param <GDS> The type of {@link GraphDatabaseService} which is used by the concrete implementation.
 */
public interface Neo4jDatastoreSession<GDS extends GraphDatabaseService> extends DatastoreSession<Long, Node, NodeMetadata, Label, Long, Relationship, RelationshipMetadata, Neo4jRelationshipType> {

    GDS getGraphDatabaseService();

}
