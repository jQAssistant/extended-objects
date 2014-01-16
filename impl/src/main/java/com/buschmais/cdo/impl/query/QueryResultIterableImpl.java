package com.buschmais.cdo.impl.query;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.impl.AbstractResultIterable;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.query.RowInvocationHandler;
import com.buschmais.cdo.impl.proxy.query.RowProxyMethodService;

import java.io.IOException;
import java.util.*;

class QueryResultIterableImpl<Entity, Relation, T> extends AbstractResultIterable<T> implements Query.Result<T> {

    private SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private ResultIterator<Map<String, Object>> iterator;
    private SortedSet<Class<?>> types;
    private RowProxyMethodService rowProxyMethodService;

    QueryResultIterableImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext,
                            ResultIterator<Map<String, Object>> iterator, SortedSet<Class<?>> types) {
        this.sessionContext = sessionContext;
        this.iterator = iterator;
        this.types = types;
        this.rowProxyMethodService = new RowProxyMethodService(sessionContext, types);
    }

    @Override
    public ResultIterator<T> iterator() {
        return new ResultIterator<T>() {

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
                return (T) sessionContext.getProxyFactory().createInstance(invocationHandler, types, CompositeRowObject.class);
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            private Object decodeValue(Object value) {
                if (value == null) {
                    return null;
                }
                Object decodedValue;
                if (sessionContext.getDatastoreSession().isEntity(value)) {
                    return sessionContext.getEntityInstanceManager().getInstance((Entity) value);
                } else if (sessionContext.getDatastoreSession().isRelation(value)) {
                    return sessionContext.getRelationInstanceManager().getInstance((Relation) value);
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

            @Override
            public void close() {
                iterator.close();
            }
        };
    }

    @Override
    public void close() throws IOException {
        iterator.close();
    }
}
