package com.buschmais.xo.impl.query;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.AbstractResultIterable;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.query.RowInvocationHandler;
import com.buschmais.xo.impl.proxy.query.RowProxyMethodService;
import com.buschmais.xo.spi.annotation.QueryDefinition;
import com.buschmais.xo.spi.reflection.AnnotatedType;

import java.io.IOException;
import java.util.*;

class QueryResultIterableImpl<Entity, Relation, T> extends AbstractResultIterable<T> implements Query.Result<T> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;
    private final ResultIterator<Map<String, Object>> iterator;
    private final SortedSet<Class<?>> returnTypes;
    private final RowProxyMethodService rowProxyMethodService;

    QueryResultIterableImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext,
                            ResultIterator<Map<String, Object>> iterator, SortedSet<Class<?>> returnTypes) {
        this.sessionContext = sessionContext;
        this.iterator = iterator;
        this.returnTypes = returnTypes;
        if (returnTypes.isEmpty() || returnTypes.size() > 1 || new AnnotatedType(returnTypes.first()).getByMetaAnnotation(QueryDefinition.class) != null) {
            this.rowProxyMethodService = new RowProxyMethodService(returnTypes);
        } else {
            this.rowProxyMethodService = null;
        }
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
                if (rowProxyMethodService != null) {
                    RowInvocationHandler invocationHandler = new RowInvocationHandler(row, rowProxyMethodService);
                    return (T) sessionContext.getProxyFactory().createInstance(invocationHandler, returnTypes.toArray(new Class<?>[0]), CompositeRowObject.class);
                }
                if (row.size() != 1) {
                    throw new XOException("Only single columns per row can be returned.");
                }
                return (T) row.values().iterator().next();
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
                if (sessionContext.getDatastoreSession().getDatastoreEntityManager().isEntity(value)) {
                    return sessionContext.getEntityInstanceManager().readInstance((Entity) value);
                } else if (sessionContext.getDatastoreSession().getDatastoreRelationManager().isRelation(value)) {
                    return sessionContext.getRelationInstanceManager().readInstance((Relation) value);
                } else if (value instanceof List<?>) {
                    decodedValue = decodeIterable((Iterable<?>) value, new ArrayList<>());
                } else if (value instanceof Set<?>) {
                    decodedValue = decodeIterable((Iterable<?>) value, new HashSet<>());
                } else if (value instanceof Iterable<?>) {
                    decodedValue = decodeIterable((Iterable<?>) value, new ArrayList<>());
                } else {
                    decodedValue = value;
                }
                return decodedValue;
            }

            private Collection<Object> decodeIterable(Iterable<?> iterable, Collection<Object> decodedCollection) {
                for (Object o : iterable) {
                    decodedCollection.add(decodeValue(o));
                }
                return decodedCollection;
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
