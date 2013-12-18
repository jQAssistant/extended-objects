package com.buschmais.cdo.impl.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.transaction.TransactionalQueryResultIterable;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

import java.util.*;

public class CdoQueryImpl<QL> implements Query {

    private QL expression;
    private DatastoreSession datastoreSession;
    private InstanceManager instanceManager;
    private CdoTransaction cdoTransaction;
    private InterceptorFactory interceptorFactory;
    private Collection<Class<?>> types;
    private Map<String, Object> parameters = null;

    public CdoQueryImpl(QL expression, DatastoreSession datastoreSession, InstanceManager instanceManager, CdoTransaction cdoTransaction, InterceptorFactory interceptorFactory, Collection<Class<?>> types) {
        this.expression = expression;
        this.datastoreSession = datastoreSession;
        this.instanceManager = instanceManager;
        this.cdoTransaction = cdoTransaction;
        this.interceptorFactory = interceptorFactory;
        this.types = types;
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
        return interceptorFactory.addInterceptor(this);
    }

    @Override
    public Query withParameters(Map<String, Object> parameters) {
        if (this.parameters != null) {
            throw new CdoException(("Parameters have already beed assigned: " + parameters));
        }
        this.parameters = parameters;
        return interceptorFactory.addInterceptor(this);
    }

    @Override
    public <T> Result<T> execute() {
        Map<String, Object> effectiveParameters = new HashMap<>();
        if (parameters != null) {
            for (Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
                String name = parameterEntry.getKey();
                Object value = parameterEntry.getValue();
                if (instanceManager.isEntity(value)) {
                    value = instanceManager.getEntity(value);
                }
                effectiveParameters.put(name, value);
            }
        }
        ResultIterator<Map<String, Object>> iterator = datastoreSession.execute(expression, effectiveParameters);
        SortedSet<Class<?>> resultTypes = getResultTypes();
        QueryResultIterableImpl queryResultIterable = new QueryResultIterableImpl(instanceManager, interceptorFactory, datastoreSession, iterator, resultTypes);
        return new TransactionalQueryResultIterable(queryResultIterable,cdoTransaction);
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
