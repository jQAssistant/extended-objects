package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.remote.impl.converter.RemoteParameterConverter;
import com.buschmais.xo.neo4j.remote.impl.converter.RemoteValueConverter;
import com.buschmais.xo.neo4j.remote.impl.model.StatementExecutor;
import com.buschmais.xo.neo4j.spi.helper.Converter;
import com.buschmais.xo.spi.datastore.DatastoreQuery;

public class RemoteDatastoreCypherQuery implements DatastoreQuery<Cypher> {

    private final StatementExecutor statementExecutor;

    private final Converter parameterConverter;

    private final Converter valueConverter;

    public RemoteDatastoreCypherQuery(StatementExecutor statementExecutor, RemoteDatastoreSessionCache datastoreSessionCache) {
        this.statementExecutor = statementExecutor;
        parameterConverter = new Converter(Arrays.asList(new RemoteParameterConverter()));
        valueConverter = new Converter(Arrays.asList(new RemoteValueConverter(datastoreSessionCache)));
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(Cypher query, Map<String, Object> parameters) {
        return execute(query.value(), parameters);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
        StatementResult result = statementExecutor.execute(query, parameterConverter.<Map<String, Object>> convert(parameters));
        return new ResultIterator<Map<String, Object>>() {

            @Override
            public boolean hasNext() {
                return result.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                Record record = result.next();
                Map<String, Object> row = record.asMap();
                Map<String, Object> result = new LinkedHashMap<>(row.size(), 1);
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String column = entry.getKey();
                    Object value = entry.getValue();
                    result.put(column, valueConverter.convert(value));
                }
                return result;
            }

            @Override
            public void close() {
                result.consume();
            }
        };
    }
}
