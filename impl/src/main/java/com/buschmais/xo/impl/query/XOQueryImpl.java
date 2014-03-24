package com.buschmais.xo.impl.query;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.transaction.TransactionalQueryResultIterable;

import java.util.*;

public class XOQueryImpl<T, QL, Entity, Relation> implements Query<T> {

    private final QL expression;
    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private final Class<?> returnType;
    private final Collection<? extends Class<?>> returnTypes;
    private Map<String, Object> parameters = null;

    public XOQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, QL expression, Class<?> returnType, Collection<? extends Class<?>> returnTypes) {
        this.sessionContext = sessionContext;
        this.expression = expression;
        this.returnType = returnType;
        this.returnTypes = returnTypes;
    }

    @Override
    public Query<T> withParameter(String name, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        Object oldValue = parameters.put(name, value);
        if (oldValue != null) {
            throw new XOException("Parameter '" + name + "' has already been assigned to value '" + value + "'.");
        }
        return sessionContext.getInterceptorFactory().addInterceptor(this);
    }

    @Override
    public Query<T> withParameters(Map<String, Object> parameters) {
        if (this.parameters != null) {
            throw new XOException(("Parameters have already been assigned: " + parameters));
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
        ResultIterator<Map<String, Object>> iterator = sessionContext.getDatastoreSession().executeQuery(expression, effectiveParameters);
        SortedSet<Class<?>> resultTypes = getResultTypes();
        QueryResultIterableImpl<Entity, Relation, Map<String, Object>> queryResultIterable = new QueryResultIterableImpl(sessionContext, iterator, returnType, resultTypes);
        return new TransactionalQueryResultIterable(queryResultIterable, sessionContext.getXOTransaction());
    }

    private SortedSet<Class<?>> getResultTypes() {
        SortedSet<Class<?>> resultTypes = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        resultTypes.addAll(returnTypes);
        if (expression instanceof Class<?>) {
            resultTypes.add((Class<?>) expression);
        }
        return resultTypes;
    }
}
