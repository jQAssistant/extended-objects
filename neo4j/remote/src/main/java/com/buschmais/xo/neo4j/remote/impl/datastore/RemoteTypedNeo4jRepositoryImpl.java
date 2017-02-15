package com.buschmais.xo.neo4j.remote.impl.datastore;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.StatementExecutor;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.session.XOSession;

/**
 * Implementation of {@link TypedNeo4jRepository}.
 * 
 * @param <T>
 */
public class RemoteTypedNeo4jRepositoryImpl<T> extends RemoteNeo4jRepositoryImpl implements TypedNeo4jRepository<T> {

    private final Class<T> type;

    public RemoteTypedNeo4jRepositoryImpl(XOSession<?, ?, NodeMetadata<RemoteLabel>, RemoteLabel, ?, ?, ?, ?, PropertyMetadata> xoSession, Class<T> type,
            StatementExecutor statementExecutor, RemoteDatastoreSessionCache sessionCache) {
        super(xoSession, statementExecutor, sessionCache);
        this.type = type;
    }

    @Override
    public ResultIterable<T> find(Object value) {
        return find(type, value);
    }

}
