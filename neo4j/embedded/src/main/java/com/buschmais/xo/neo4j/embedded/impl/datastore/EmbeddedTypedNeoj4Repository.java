package com.buschmais.xo.neo4j.embedded.impl.datastore;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.spi.session.XOSession;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Implementation of {@link TypedNeo4jRepository}.
 * 
 * @param <T>
 */
public class EmbeddedTypedNeoj4Repository<T> extends EmbeddedNeo4jRepository implements TypedNeo4jRepository<T> {

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
    public EmbeddedTypedNeoj4Repository(Class<T> type, GraphDatabaseService graphDatabaseService,
            XOSession<NodeMetadata<EmbeddedLabel>, EmbeddedLabel, ?, ?> xoSession) {
        super(graphDatabaseService, xoSession);
        this.type = type;
    }

    @Override
    public ResultIterable<T> find(Object value) {
        return find(type, value);
    }

}
