package com.buschmais.xo.neo4j.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.impl.datastore.metadata.RelationshipType;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Implementation of {@link TypedNeo4jRepository}.
 * 
 * @param <T>
 */
public class TypedNeoj4RepositoryImpl<T> extends AbstractNeo4jRepositoryImpl implements TypedNeo4jRepository<T> {

    private Class<T> type;

    /**
     * Constructor.
     * 
     * @param type
     *            The repository type.
     * @param graphDatabaseService
     *            The graph database service.
     * @param xoSession
     *            The {@link XOSession}.
     */
    public TypedNeoj4RepositoryImpl(Class<T> type, GraphDatabaseService graphDatabaseService,
            XOSession<Long, Neo4jNode, NodeMetadata, Neo4jLabel, Long, Neo4jRelationship, RelationshipMetadata, RelationshipType, PropertyMetadata> xoSession) {
        super(graphDatabaseService, xoSession);
        this.type = type;
    }

    @Override
    public ResultIterable<T> find(Object value) {
        return find(type, value);
    }

}
