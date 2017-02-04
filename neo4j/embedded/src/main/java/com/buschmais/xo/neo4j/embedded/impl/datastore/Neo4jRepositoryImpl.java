package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Implementation of the {@link Neo4jRepository}.
 */
public class Neo4jRepositoryImpl extends AbstractNeo4jRepositoryImpl implements Neo4jRepository {

    protected Neo4jRepositoryImpl(GraphDatabaseService graphDatabaseService,
            XOSession<Long, EmbeddedNode, NodeMetadata, EmbeddedLabel, Long, EmbeddedRelationship, RelationshipMetadata, EmbeddedRelationshipType, PropertyMetadata> xoSession) {
        super(graphDatabaseService, xoSession);
    }

    @Override
    public <T> ResultIterable<T> find(Class<T> type, Object value) {
        return super.find(type, value);
    }

}
