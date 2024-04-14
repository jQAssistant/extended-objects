package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.spi.CypherQuery;
import com.buschmais.xo.neo4j.spi.CypherQueryResultIterator;

import org.neo4j.graphdb.Result;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class EmbeddedCypherQuery implements CypherQuery {

    private EmbeddedDatastoreSessionImpl embeddedNeo4jDatastoreSession;

    public EmbeddedCypherQuery(EmbeddedDatastoreSessionImpl embeddedNeo4jDatastoreSession) {
        this.embeddedNeo4jDatastoreSession = embeddedNeo4jDatastoreSession;
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(Cypher expression, Map<String, Object> parameters) {
        return execute(expression.value(), parameters);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(String expression, Map<String, Object> parameters) {
        Map<String, Object> convertedParameters = (Map<String, Object>) embeddedNeo4jDatastoreSession.convertParameter(parameters);
        EmbeddedDatastoreTransaction datastoreTransaction = embeddedNeo4jDatastoreSession.getDatastoreTransaction();
        if (datastoreTransaction.isActive()) {
            return executeTransactional(expression, convertedParameters, datastoreTransaction);
        } else {
            return executeNonTransactional(expression, convertedParameters);
        }
    }

    private ResultIterator<Map<String, Object>> executeTransactional(String expression, Map<String, Object> parameters,
        EmbeddedDatastoreTransaction datastoreTransaction) {
        Result executionResult = datastoreTransaction.getTransaction()
            .execute(expression, parameters);
        List<String> columns = executionResult.columns();
        return new ResultIterator<>() {

            @Override
            public boolean hasNext() {
                return executionResult.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                return convertRow(executionResult.next(), columns);
            }

            @Override
            public void remove() {
                throw new XOException("Remove operation is not supported for query results.");
            }

            @Override
            public void close() {
                executionResult.close();
            }
        };
    }

    private ResultIterator<Map<String, Object>> executeNonTransactional(String expression, Map<String, Object> parameters) {
        return embeddedNeo4jDatastoreSession.getGraphDatabaseService()
            .executeTransactionally(expression, parameters, result -> {
                List<String> columns = result.columns();
                List<Map<String, Object>> rows = result.stream()
                    .map(row -> convertRow(row, columns))
                    .collect(toList());
                Iterator<Map<String, Object>> iterator = rows.iterator();
                return new CypherQueryResultIterator() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Map<String, Object> next() {
                        return iterator.next();
                    }

                    @Override
                    public void remove() {
                        throw new XOException("Remove operation is not supported for query results.");
                    }

                    @Override
                    public List<Notification> dispose() {
                        return stream(result.getNotifications()
                            .spliterator(), false).map(n -> Notification.builder()
                                .title(n.getTitle())
                                .description(n.getDescription())
                                .code(n.getCode())
                                .severity(n.getSeverity()
                                    .name())
                                .offset(n.getPosition()
                                    .getOffset())
                                .line(n.getPosition()
                                    .getLine())
                                .column(n.getPosition()
                                    .getColumn())
                                .build())
                            .collect(toList());
                    }
                };
            });
    }

    private Map<String, Object> convertRow(Map<String, Object> row, List<String> columns) {
        Map<String, Object> result = new LinkedHashMap<>(row.size(), 1);
        for (String column : columns) {
            result.put(column, embeddedNeo4jDatastoreSession.convertValue(row.get(column)));
        }
        return result;
    }
}
