package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.Arrays;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.remote.impl.converter.RemoteEntityConverter;
import com.buschmais.xo.neo4j.remote.impl.converter.RemoteParameterConverter;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jRepository;
import com.buschmais.xo.neo4j.spi.helper.Converter;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.session.XOSession;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Values;

/**
 * Implementation of the {@link Neo4jRepository}.
 */
public class RemoteNeo4jRepositoryImpl extends AbstractNeo4jRepository<RemoteLabel> {

    private final StatementExecutor statementExecutor;

    private final Converter parameterConverter;

    private final Converter valueConverter;

    public RemoteNeo4jRepositoryImpl(XOSession<NodeMetadata<RemoteLabel>, RemoteLabel, ?, ?> xoSession, StatementExecutor statementExecutor,
            RemoteDatastoreSessionCache sessionCache) {
        super(xoSession);
        this.statementExecutor = statementExecutor;
        this.parameterConverter = new Converter(Arrays.asList(new RemoteParameterConverter()));
        this.valueConverter = new Converter(Arrays.asList(new RemoteEntityConverter(sessionCache)));
    }

    @Override
    protected <T> ResultIterable<T> find(RemoteLabel label, PropertyMetadata datastoreMetadata, Object value) {
        String statement = String.format("MATCH (n:%s{%s:{value}}) RETURN n", label.getName(), datastoreMetadata.getName());
        StatementResult statementResult = statementExecutor.execute(statement, Values.parameters("value", parameterConverter.convert(value)));
        return xoSession.toResult(new ResultIterator<RemoteNode>() {

            @Override
            public boolean hasNext() {
                return statementResult.hasNext();
            }

            @Override
            public RemoteNode next() {
                Record record = statementResult.next();
                return valueConverter.convert(record.get("n").asNode());
            }

            @Override
            public void close() {
                statementResult.consume();
            }
        });
    }

}
