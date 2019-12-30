package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.spi.helper.Converter;
import com.buschmais.xo.spi.datastore.DatastoreQuery;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class RemoteDatastoreCypherQuery implements DatastoreQuery<Cypher> {

    private final StatementExecutor statementExecutor;

    private final Converter parameterConverter;

    private final Converter valueConverter;

    public RemoteDatastoreCypherQuery(StatementExecutor statementExecutor, Converter parameterConverter, Converter valueConverter) {
        this.statementExecutor = statementExecutor;
        this.parameterConverter = parameterConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(Cypher query, Map<String, Object> parameters) {
        return execute(query.value(), parameters);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
        Result result = statementExecutor.execute(query, parameterConverter.<Map<String, Object>> convert(parameters));
        return new ResultIterator<Map<String, Object>>() {

            @Override
            public boolean hasNext() {
                return result.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
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
