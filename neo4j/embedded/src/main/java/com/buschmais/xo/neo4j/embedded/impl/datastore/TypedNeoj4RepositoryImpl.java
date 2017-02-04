package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
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
            XOSession<Long, EmbeddedNode, NodeMetadata, EmbeddedLabel, Long, EmbeddedRelationship, RelationshipMetadata, EmbeddedRelationshipType, PropertyMetadata> xoSession) {
        super(graphDatabaseService, xoSession);
        this.type = type;
    }

    @Override
    public ResultIterable<T> find(Object value) {
        return find(type, value);
    }

}
