package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.impl.common.AbstractIterableResult;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.query.proxy.RowInvocationHandler;
import com.buschmais.cdo.neo4j.impl.query.proxy.method.RowProxyMethodService;
import org.neo4j.graphdb.Node;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

class IterableQueryResultImpl<T> extends AbstractIterableResult<T> implements Query.Result<T> {

    private InstanceManager instanceManager;
    private Iterator<Map<String, Object>> iterator;
    private List<Class<?>> types;
    private RowProxyMethodService rowProxyMethodService;

    IterableQueryResultImpl(InstanceManager instanceManager, Iterator<Map<String, Object>> iterator, List<Class<?>> types) {
        this.instanceManager = instanceManager;
        this.iterator = iterator;
        this.types = types;
        this.rowProxyMethodService = new RowProxyMethodService(types);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                Map<String, Object> next = iterator.next();
                Map<String, Object> row = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : next.entrySet()) {
                    String column = entry.getKey();
                    Object value = entry.getValue();
                    Object decodedValue = decodeValue(value);
                    row.put(column, decodedValue);
                }
                RowInvocationHandler invocationHandler = new RowInvocationHandler(row, rowProxyMethodService);
                return (T) instanceManager.createInstance(invocationHandler, types, CompositeRowObject.class);
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
        if (iterator instanceof Closeable) {
            ((Closeable) iterator).close();
        }
    }
}
