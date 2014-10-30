package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.spi.session.XOSession;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import org.neo4j.graphdb.*;

/**
 * Implementation of the {@link Neo4jRepository}.
 */
public class Neo4jRepositoryImpl extends AbstractNeo4jRepositoryImpl implements Neo4jRepository {

    protected Neo4jRepositoryImpl(GraphDatabaseService graphDatabaseService,
            XOSession<Long, Node, NodeMetadata, Label, Long, Relationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession) {
        super(graphDatabaseService, xoSession);
    }

    @Override
    public <T> ResultIterable<T> find(Class<T> type, Object value) {
        return super.find(type, value);
    }

}
