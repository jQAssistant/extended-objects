package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.neo4j.api.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.impl.datastore.metadata.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.lang.annotation.Annotation;

/**
 * Abstract base implementation of a Neo4j database session based on the
 * {@link org.neo4j.graphdb.GraphDatabaseService} API.
 *
 * @param <GDS> The type of {@link org.neo4j.graphdb.GraphDatabaseService}.
 */
public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements Neo4jDatastoreSession<GDS> {

    private final GDS graphDatabaseService;
    private final Neo4jDatastoreEntityManager entityManager;
    private final Neo4jDatastoreRelationManager relationManager;

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
        this.entityManager = new Neo4jDatastoreEntityManager(graphDatabaseService);
        this.relationManager = new Neo4jDatastoreRelationManager();
    }

    @Override
    public DatastoreEntityManager<Long, Node, NodeMetadata, Label, ?> getDatastoreEntityManager() {
        return entityManager;
    }

    @Override
    public DatastoreRelationManager<Node, Long, Relationship, RelationshipMetadata, Neo4jRelationshipType, ?> getDatastoreRelationManager() {
        return relationManager;
    }

    @Override
    public Class<? extends Annotation> getDefaultQueryLanguage() {
        return Cypher.class;
    }

    @Override
    public GDS getGraphDatabaseService() {
        return graphDatabaseService;
    }

    @Override
    public void close() {
    }
}

