package com.buschmais.xo.neo4j.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Implementation of the {@link Neo4jRepository}.
 */
public class Neo4jRepositoryImpl extends AbstractNeo4jRepositoryImpl implements Neo4jRepository {

    protected Neo4jRepositoryImpl(GraphDatabaseService graphDatabaseService,
            XOSession<Long, Neo4jNode, NodeMetadata, Neo4jLabel, Long, Neo4jRelationship, RelationshipMetadata, Neo4jRelationshipType, PropertyMetadata> xoSession) {
        super(graphDatabaseService, xoSession);
    }

    @Override
    public <T> ResultIterable<T> find(Class<T> type, Object value) {
        return super.find(type, value);
    }

}
