package com.buschmais.xo.impl.query;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.plugin.QueryLanguagePluginRepository;
import com.buschmais.xo.impl.transaction.TransactionalResultIterator;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.plugin.QueryLanguagePlugin;

/**
 * Implementation of a {@link com.buschmais.xo.api.Query}.
 * 
 * @param <T>
 *            The result type.
 * @param <QL>
 *            The query language type.
 * @param <QE>
 *            The query expression type.
 * @param <Entity>
 *            The entity type.
 * @param <Relation>
 *            The relation type.
 */
public class XOQueryImpl<T, QL extends Annotation, QE, Entity, Relation> implements Query<T> {

    private Class<? extends Annotation> queryLanguage = null;
    private final QE expression;
    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;
    private final QueryLanguagePluginRepository queryLanguagePluginManager;
    private final Class<?> returnType;
    private final Collection<? extends Class<?>> returnTypes;
    private final AbstractInstanceManager<?, Entity> entityInstanceManager;
    private final AbstractInstanceManager<?, Relation> relationInstanceManager;
    private Map<String, Object> parameters = null;

    public XOQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, QE expression, Class<?> returnType,
            Collection<? extends Class<?>> returnTypes) {
        this.sessionContext = sessionContext;
        this.entityInstanceManager = sessionContext.getEntityInstanceManager();
        this.relationInstanceManager = sessionContext.getRelationInstanceManager();
        this.queryLanguagePluginManager = sessionContext.getPluginRepositoryManager().getPluginManager(QueryLanguagePlugin.class);
        this.expression = expression;
        this.returnType = returnType;
        this.returnTypes = returnTypes;
    }

    public XOQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, QE expression) {
        this(sessionContext, expression, null, Collections.<Class<?>> emptyList());
    }

    public XOQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, QE expression, Class<?> returnType) {
        this(sessionContext, expression, returnType, Collections.<Class<?>> emptyList());
    }

    @Override
    public Query<T> using(Class<? extends Annotation> queryLanguage) {
        this.queryLanguage = queryLanguage;
        return sessionContext.getInterceptorFactory().addInterceptor(this, Query.class);
    }

    @Override
    public Query<T> withParameter(String name, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        Object oldValue = parameters.put(name, convertParameter(value));
        if (oldValue != null) {
            throw new XOException("Parameter '" + name + "' has already been assigned to value '" + value + "'.");
        }
        return sessionContext.getInterceptorFactory().addInterceptor(this, Query.class);
    }

    @Override
    public Query<T> withParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new XOException("Parameters must not be null.");
        }
        if (this.parameters != null) {
            throw new XOException("Parameters have already been assigned: " + parameters);
        }
        Map<String, Object> convertedParameters = new HashMap<>(parameters.size(), 1);
        for (Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
            String name = parameterEntry.getKey();
            Object value = parameterEntry.getValue();
            value = convertParameter(value);
            convertedParameters.put(name, value);
        }
        this.parameters = convertedParameters;
        return sessionContext.getInterceptorFactory().addInterceptor(this, Query.class);
    }

    @Override
    public Result<T> execute() {
        DatastoreSession<?, Entity, ? extends DatastoreEntityMetadata<?>, ?, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> datastoreSession = sessionContext
                .getDatastoreSession();
        if (queryLanguage == null) {
            queryLanguage = datastoreSession.getDefaultQueryLanguage();
        }
        DatastoreQuery<QL> query;
        QueryLanguagePlugin<QL> queryLanguagePlugin = (QueryLanguagePlugin<QL>) queryLanguagePluginManager.get(queryLanguage);
        if (queryLanguagePlugin != null) {
            query = queryLanguagePlugin.createQuery(sessionContext.getDatastoreSession());
        } else {
            query = (DatastoreQuery<QL>) sessionContext.getDatastoreSession().createQuery(queryLanguage);
        }
        Map<String, Object> effectiveParameters = parameters != null ? parameters : Collections.emptyMap();
        ResultIterator<Map<String, Object>> iterator;
        if (expression instanceof String) {
            iterator = query.execute((String) expression, effectiveParameters);
        } else if (expression instanceof AnnotatedElement) {
            AnnotatedElement typeExpression = (AnnotatedElement) expression;
            QL queryAnnotation = sessionContext.getMetadataProvider().getQuery(typeExpression);
            if (queryAnnotation == null) {
                throw new XOException("Cannot find query annotation on element " + expression.toString());
            }
            iterator = query.execute(queryAnnotation, effectiveParameters);
        } else {
            throw new XOException("Expression type is not supported: " + expression);
        }
        SortedSet<Class<?>> resultTypes = getResultTypes();
        XOTransaction xoTransaction = sessionContext.getXOTransaction();
        return sessionContext.getInterceptorFactory().addInterceptor(
                new QueryResultIterableImpl(sessionContext, xoTransaction != null ? new TransactionalResultIterator<>(iterator, xoTransaction) : iterator,
                        resultTypes), Result.class);
    }

    /**
     * Converts the given parameter value to instances which can be passed to
     * the datastore.
     * 
     * @param value
     *            The value.
     * @return The converted value.
     */
    private Object convertParameter(Object value) {
        if (entityInstanceManager.isInstance(value)) {
            value = entityInstanceManager.getDatastoreType(value);
        } else if (relationInstanceManager.isInstance(value)) {
            value = relationInstanceManager.getDatastoreType(value);
        }
        return value;
    }

    private SortedSet<Class<?>> getResultTypes() {
        SortedSet<Class<?>> resultTypes = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
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
