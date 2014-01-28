package com.buschmais.cdo.impl.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.transaction.TransactionalQueryResultIterable;

import java.util.*;

public class CdoQueryImpl<T, QL, Entity, Relation> implements Query<T> {

    private final QL expression;
    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private final Collection<Class<?>> types;
    private Map<String, Object> parameters = null;

    public CdoQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, QL expression, Collection<Class<?>> types) {
        this.sessionContext = sessionContext;
        this.expression = expression;
        this.types = types;
    }

    @Override
    public Query<T> withParameter(String name, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        Object oldValue = parameters.put(name, value);
        if (oldValue != null) {
            throw new CdoException("Parameter '" + name + "' has already been assigned to value '" + value + "'.");
        }
        return sessionContext.getInterceptorFactory().addInterceptor(this);
    }

    @Override
    public Query<T> withParameters(Map<String, Object> parameters) {
        if (this.parameters != null) {
            throw new CdoException(("Parameters have already been assigned: " + parameters));
        }
        this.parameters = parameters;
        return sessionContext.getInterceptorFactory().addInterceptor(this);
    }

    @Override
    public Result<T> execute() {
        Map<String, Object> effectiveParameters = new HashMap<>();
        if (parameters != null) {
            AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
            AbstractInstanceManager<?, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
            for (Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
                String name = parameterEntry.getKey();
                Object value = parameterEntry.getValue();
                if (entityInstanceManager.isInstance(value)) {
                    value = entityInstanceManager.getDatastoreType(value);
                } else if (relationInstanceManager.isInstance(value)) {
                    value = relationInstanceManager.getDatastoreType(value);
                }
                effectiveParameters.put(name, value);
            }
        }
        ResultIterator<Map<String, Object>> iterator = sessionContext.getDatastoreSession().execute(expression, effectiveParameters);
        SortedSet<Class<?>> resultTypes = getResultTypes();
        QueryResultIterableImpl<Entity, Relation, Map<String, Object>> queryResultIterable = new QueryResultIterableImpl(sessionContext, iterator, resultTypes);
        return new TransactionalQueryResultIterable(queryResultIterable, sessionContext.getCdoTransaction());
    }

    private SortedSet<Class<?>> getResultTypes() {
        SortedSet<Class<?>> resultTypes = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        resultTypes.addAll(types);
        if (expression instanceof Class<?>) {
            resultTypes.add((Class<?>) expression);
        }
        return resultTypes;
    }
}
