package com.buschmais.xo.impl.query;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.transaction.TransactionalQueryResultIterable;
import com.buschmais.xo.spi.annotation.QueryDefinition;

public class XOQueryImpl<T, QL, Entity, Relation> implements Query<T> {

    private QL expression;
    private NativeQuery<?> nativeQuery;
    private Class<? extends Annotation> queryLanguage;

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private final Class<?> returnType;
    private final Collection<? extends Class<?>> returnTypes;
    private Map<String, Object> parameters = null;

    public XOQueryImpl(final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, final NativeQuery<?> query, final Class<?> returnType, final Collection<? extends Class<?>> returnTypes) {
        this.sessionContext = sessionContext;
        this.nativeQuery = query;
        this.returnType = returnType;
        this.returnTypes = returnTypes;
    }

    public XOQueryImpl(final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, final NativeQuery<?> query) {
        this(sessionContext, query, null, Collections.<Class<?>>emptyList());
    }

    public XOQueryImpl(final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, final NativeQuery<?> query, final Class<?> returnType) {
        this(sessionContext, query, returnType, Collections.<Class<?>>emptyList());
    }

    public XOQueryImpl(final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, final QL expression, final Class<?> returnType, final Collection<? extends Class<?>> returnTypes) {
        this.sessionContext = sessionContext;
        this.expression = expression;
        this.returnType = returnType;
        this.returnTypes = returnTypes;
    }

    public XOQueryImpl(final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, final QL expression) {
        this(sessionContext, expression, null, Collections.<Class<?>>emptyList());
    }

    public XOQueryImpl(final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, final QL expression, final Class<?> returnType) {
        this(sessionContext, expression, returnType, Collections.<Class<?>>emptyList());
    }

    @Override
    public Query<T> withParameter(final String name, final Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        final Object oldValue = parameters.put(name, value);
        if (oldValue != null) {
            throw new XOException("Parameter '" + name + "' has already been assigned to value '" + value + "'.");
        }
        return sessionContext.getInterceptorFactory().addInterceptor(this);
    }

    @Override
    public Query<T> withParameters(final Map<String, Object> parameters) {
        if (this.parameters != null) {
            throw new XOException(("Parameters have already been assigned: " + parameters));
        }
        this.parameters = parameters;
        return sessionContext.getInterceptorFactory().addInterceptor(this);
    }

    @Override
    public Query<T> using(final Class<? extends Annotation> language) {
        if (!language.isAnnotationPresent(QueryDefinition.class)) {
            throw new XOException("");
        }
        this.queryLanguage = language;
        return sessionContext.getInterceptorFactory().addInterceptor(this);
    }

    @Override
    public Result<T> execute() {
        final Map<String, Object> effectiveParameters = new HashMap<>();
        if (parameters != null) {
            final AbstractInstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
            final AbstractInstanceManager<?, Relation> relationInstanceManager = sessionContext.getRelationInstanceManager();
            for (final Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
                final String name = parameterEntry.getKey();
                Object value = parameterEntry.getValue();
                if (entityInstanceManager.isInstance(value)) {
                    value = entityInstanceManager.getDatastoreType(value);
                } else if (relationInstanceManager.isInstance(value)) {
                    value = relationInstanceManager.getDatastoreType(value);
                }
                effectiveParameters.put(name, value);
            }
        }

        if (nativeQuery == null) {
            if (expression instanceof String) {
                nativeQuery = sessionContext.getDatastoreSession().getNativeQuery((String) expression, queryLanguage);
            } else if (expression instanceof AnnotatedElement) {
                nativeQuery = sessionContext.getDatastoreSession().getNativeQuery((AnnotatedElement) expression, queryLanguage);
            } else {
                throw new XOException("");
            }
        }
        final ResultIterator<Map<String, Object>> iterator = sessionContext.getDatastoreSession().executeQuery(nativeQuery, effectiveParameters);

        final SortedSet<Class<?>> resultTypes = getResultTypes();
        final QueryResultIterableImpl<Entity, Relation, Map<String, Object>> queryResultIterable = new QueryResultIterableImpl(sessionContext, iterator, resultTypes);
        return new TransactionalQueryResultIterable(queryResultIterable, sessionContext.getXOTransaction());
    }

    private SortedSet<Class<?>> getResultTypes() {
        final SortedSet<Class<?>> resultTypes = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(final Class<?> o1, final Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        if (returnType != null) {
            resultTypes.add(returnType);
        }
        resultTypes.addAll(returnTypes);
        return resultTypes;
    }
}
