package com.buschmais.xo.impl.query;

import java.util.Map;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.AbstractResultIterable;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.converter.ValueConverter;
import com.buschmais.xo.impl.proxy.query.RowProxyMethodService;

class QueryResultIterableImpl<Entity, Relation, T> extends AbstractResultIterable<T> implements Query.Result<T> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;
    private final ResultIterator<Map<String, Object>> iterator;
    private final Class<T> rowType;
    private final ValueConverter<Entity, Relation> valueConverter;
    private final RowProxyMethodService<Entity, Relation> rowProxyMethodService;

    QueryResultIterableImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, ResultIterator<Map<String, Object>> iterator,
        Class<T> rowType) {
        this.sessionContext = sessionContext;
        this.iterator = iterator;
        // can be null if execute() is called without consuming the result
        this.rowType = rowType;
        this.valueConverter = new ValueConverter<>(sessionContext);
        this.rowProxyMethodService = rowType == null || valueConverter.isTypedQuery(rowType) ? new RowProxyMethodService<>(rowType, sessionContext) : null;
    }

    @Override
    public ResultIterator<T> iterator() {
        return sessionContext.getInterceptorFactory()
            .addInterceptor(new ResultIterator<T>() {

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public T next() {
                    Map<String, Object> row = iterator.next();
                    if (rowProxyMethodService != null) {
                        return valueConverter.convert(row, rowProxyMethodService);
                    } else if (row.size() > 1 && valueConverter.isProjection(rowType)) {
                        RowProxyMethodService<Entity, Relation> rowProxyMethodService = new RowProxyMethodService<>(rowType, sessionContext);
                        return valueConverter.convert(row, rowProxyMethodService);
                    }
                    Object singleValue = row.values()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new XOException("Only single columns per row can be returned."));
                    return valueConverter.convert(singleValue, rowType);
                }

                @Override
                public void remove() {
                    iterator.remove();
                }

                @Override
                public void close() {
                    iterator.close();
                }
            }, ResultIterator.class);
    }

    @Override
    public void close() {
        iterator.close();
    }
}
