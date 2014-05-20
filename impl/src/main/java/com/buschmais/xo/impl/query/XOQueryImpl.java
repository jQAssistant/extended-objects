package com.buschmais.xo.impl.query;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.plugin.QueryLanguagePluginRepository;
import com.buschmais.xo.impl.transaction.TransactionalQueryResultIterable;
import com.buschmais.xo.spi.annotation.QueryDefinition;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.plugin.QueryLanguagePlugin;
import com.buschmais.xo.spi.reflection.AbstractAnnotatedElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

public class XOQueryImpl<T, QL extends Annotation, QE, Entity, Relation> implements Query<T> {

    private Class<? extends Annotation> queryLanguage = null;
    private final QE expression;
    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private final QueryLanguagePluginRepository queryLanguagePluginManager;
    private final Class<?> returnType;
    private final Collection<? extends Class<?>> returnTypes;
    private Map<String, Object> parameters = null;

    public XOQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, QE expression, Class<?> returnType,
                       Collection<? extends Class<?>> returnTypes) {
        this.sessionContext = sessionContext;
        this.queryLanguagePluginManager = sessionContext.getPluginRepositoryManager().getPluginManager(QueryLanguagePlugin.class);
        this.expression = expression;
        this.returnType = returnType;
        this.returnTypes = returnTypes;
    }

    public XOQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, QE expression) {
        this(sessionContext, expression, null, Collections.<Class<?>>emptyList());
    }

    public XOQueryImpl(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, QE expression, Class<?> returnType) {
        this(sessionContext, expression, returnType, Collections.<Class<?>>emptyList());
    }

    @Override
    public Query<T> using(Class<? extends Annotation> queryLanguage) {
        this.queryLanguage = queryLanguage;
        return sessionContext.getInterceptorFactory().addInterceptor(this);
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
        DatastoreSession<?, Entity, ? extends DatastoreEntityMetadata<?>, ?, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?> datastoreSession = sessionContext
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
        ResultIterator<Map<String, Object>> iterator;
        if (expression instanceof String) {
            iterator = query.execute((String) expression, effectiveParameters);
        } else if (expression instanceof AnnotatedElement) {
            AnnotatedElement typeExpression = (AnnotatedElement) expression;
            AnnotatedQueryElement element = new AnnotatedQueryElement(typeExpression);
            QL queryAnnotation = element.getByMetaAnnotation(QueryDefinition.class);
            if (queryAnnotation == null) {
                throw new XOException("Cannot find query annotation on element " + expression.toString());
            }
            iterator = query.execute(queryAnnotation, effectiveParameters);
        } else {
            throw new XOException("Expression type is not supported: " + expression);
        }
        SortedSet<Class<?>> resultTypes = getResultTypes();
        QueryResultIterableImpl<Entity, Relation, Map<String, Object>> queryResultIterable = new QueryResultIterableImpl(sessionContext, iterator, resultTypes);
        return new TransactionalQueryResultIterable(queryResultIterable, sessionContext.getXOTransaction());
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

    /**
     * An annotated element.
     */
    private static class AnnotatedQueryElement extends AbstractAnnotatedElement<AnnotatedElement> {

        /**
         * Constructor.
         *
         * @param typeExpression The expression.
         */
        public AnnotatedQueryElement(AnnotatedElement typeExpression) {
            super(typeExpression);
        }

        @Override
        public String getName() {
            return toString();
        }
    }
}
