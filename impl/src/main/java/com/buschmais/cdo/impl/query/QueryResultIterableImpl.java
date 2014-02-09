package com.buschmais.cdo.impl.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.impl.AbstractResultIterable;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.query.RowInvocationHandler;
import com.buschmais.cdo.impl.proxy.query.RowProxyMethodService;
import com.buschmais.cdo.spi.annotation.QueryDefinition;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.io.IOException;
import java.util.*;

class QueryResultIterableImpl<Entity, Relation, T> extends AbstractResultIterable<T> implements Query.Result<T> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private final ResultIterator<Map<String, Object>> iterator;
    private final SortedSet<Class<?>> returnTypes;
    private final RowProxyMethodService rowProxyMethodService;

    QueryResultIterableImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext,
                            ResultIterator<Map<String, Object>> iterator, Class<?> returnType, SortedSet<Class<?>> returnTypes) {
        this.sessionContext = sessionContext;
        this.iterator = iterator;
        this.returnTypes = returnTypes;
        if (CompositeRowObject.class.equals(returnType) || new AnnotatedType(returnType).getByMetaAnnotation(QueryDefinition.class) != null) {
            this.rowProxyMethodService = new RowProxyMethodService<>(sessionContext, returnTypes);
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
                    return (T) sessionContext.getProxyFactory().createInstance(invocationHandler, returnTypes, CompositeRowObject.class);
                }
                if (row.size() != 1) {
                    throw new CdoException("Only single columns per row can be returned.");
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
                if (sessionContext.getDatastoreSession().isEntity(value)) {
                    return sessionContext.getEntityInstanceManager().getInstance((Entity) value);
                } else if (sessionContext.getDatastoreSession().isRelation(value)) {
                    return sessionContext.getRelationInstanceManager().getInstance((Relation) value);
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
