package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.IterableResult;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.impl.proxy.AbstractIterableResult;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public abstract class AbstractCypherQueryImpl<QL> implements Query {

    private ExecutionEngine executionEngine;

    private InstanceManager instanceManager;

    private QL expression;

    private Map<String, Object> parameters = null;

    public AbstractCypherQueryImpl(QL expression, ExecutionEngine executionEngine, InstanceManager instanceManager) {
        this.expression = expression;
        this.executionEngine = executionEngine;
        this.instanceManager = instanceManager;
    }

    @Override
    public Query withParameter(String name, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        Object oldValue = parameters.put(name, value);
        if (oldValue != null) {
            throw new CdoException("Parameter '" + name + "' has alread been assigned to value '" + value + "'.");
        }
        return this;
    }

    @Override
    public Query withParameters(Map<String, Object> parameters) {
        if (parameters != null) {
            throw new CdoException(("Parameters have already beed assigned: " + parameters));
        }
        this.parameters = parameters;
        return this;
    }

    @Override
    public Result execute() {
        String query = getQuery();
        ExecutionResult result = executionEngine.execute(query, parameters != null ? parameters : Collections.<String, Object>emptyMap());
        IterableResult<Result.Row> rowIterable = new RowIterable(result.columns(), result.iterator());
        return new QueryResultImpl(result.columns(), rowIterable);
    }

    protected QL getExpression() {
        return expression;
    }

    protected abstract String getQuery();

    private final class RowIterable extends AbstractIterableResult<Result.Row> implements Closeable {

        private List<String> columns;
        private ResourceIterator<Map<String, Object>> iterator;

        private RowIterable(List<String> columns, ResourceIterator<Map<String, Object>> iterator) {
            this.columns = columns;
            this.iterator = iterator;
        }

        @Override
        public Iterator<Result.Row> iterator() {

            return new Iterator<Result.Row>() {

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Result.Row next() {
                    Map<String, Object> next = iterator.next();
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String column : columns) {
                        Object value = next.get(column);
                        Object decodedValue = decodeValue(value);
                        row.put(column, decodedValue);
                    }
                    return new QueryResultRow(row);
                }

                @Override
                public void remove() {
                    iterator.remove();
                }

                private Object decodeValue(Object value) {
                    Object decodedValue;
                    if (value instanceof Node) {
                        Node node = (Node) value;
                        return instanceManager.getInstance(node);
                    } else if (value instanceof List<?>) {
                        List<?> listValue = (List<?>) value;
                        List<Object> decodedList = new ArrayList<>();
                        for (Object o : listValue) {
                            decodedList.add(decodeValue(o));
                        }
                        decodedValue = decodedList;
                    } else {
                        decodedValue = value;
                    }
                    return decodedValue;
                }
            };
        }

        @Override
        public void close() throws IOException {
            iterator.close();
        }
    }
}
