package com.buschmais.xo.neo4j.embedded.api;

import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.neo4j.spi.Neo4jDatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreSession;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Defines the Neo4j specific {@link DatastoreSession} interface.
 */
public interface EmbeddedNeo4jDatastoreSession extends Neo4jDatastoreSession<EmbeddedNode, EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType> {

    GraphDatabaseService getGraphDatabaseService();

}
