package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.*;
import com.buschmais.cdo.neo4j.impl.cache.TransactionalCache;
import com.buschmais.cdo.neo4j.impl.common.AbstractResultIterable;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.query.CypherStringQueryImpl;
import com.buschmais.cdo.neo4j.impl.query.CypherTypeQueryImpl;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import org.neo4j.graphdb.Node;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class Neo4jCdoManagerImpl implements CdoManager {

    private final InstanceManager<Long, Node> instanceManager;
    private final TransactionalCache cache;
    private final DatastoreSession<Long, Node> datastoreSession;
    private final ValidatorFactory validatorFactory;

    public Neo4jCdoManagerImpl(DatastoreSession<Long, Node> datastoreSession, InstanceManager instanceManager, TransactionalCache cache, ValidatorFactory validatorFactory) {
        this.instanceManager = instanceManager;
        this.cache = cache;
        this.validatorFactory = validatorFactory;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public void begin() {
        datastoreSession.begin();
    }

    @Override
    public void commit() {
        Set<ConstraintViolation<Object>> constraintViolations = validate();
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        datastoreSession.commit();
        cache.afterCompletion(true);
    }

    @Override
    public Set<ConstraintViolation<Object>> validate() {
        if (validatorFactory == null) {
            return Collections.emptySet();
        }
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        for (Object instance : new ArrayList(cache.values())) {
            violations.addAll(validator.validate(instance));
        }
        return violations;
    }

    @Override
    public void rollback() {
        datastoreSession.rollback();
        cache.afterCompletion(false);
    }

    @Override
    public <T> ResultIterable<T> find(final Class<T> type, final Object value) {
        final ResultIterator<Node> iterator = datastoreSession.find(type, value);
        return new AbstractResultIterable<T>() {
            @Override
            public ResultIterator<T> iterator() {
                return new ResultIterator<T>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public T next() {
                        Node node = iterator.next();
                        return instanceManager.getInstance(node);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Cannot remove instance.");
                    }

                    @Override
                    public void close() {
                        iterator.close();
                    }
                };
            }
        };
    }

    @Override
    public CompositeObject create(Class type, Class<?>... types) {
        List<Class<?>> effectiveTypes = getEffectiveTypes(type, types);
        Node node = datastoreSession.create(effectiveTypes);
        return instanceManager.getInstance(node);
    }

    public <T> T create(Class<T> type) {
        return create(type, new Class<?>[0]).as(type);
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType, Class<?>... targetTypes) {
        Node node = instanceManager.getEntity(instance);
        List<Class<?>> types = datastoreSession.getTypes(node);
        List<Class<?>> effectiveTargetTypes = getEffectiveTypes(targetType, targetTypes);
        datastoreSession.migrate(node, types, effectiveTargetTypes);
        instanceManager.removeInstance(instance);
        CompositeObject migratedInstance = instanceManager.getInstance(node);
        if (migrationStrategy != null) {
            migrationStrategy.migrate(instance, migratedInstance.as(targetType));
        }
        instanceManager.destroyInstance(instance);
        return migratedInstance;
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, Class<M> targetType, Class<?>... targetTypes) {
        return migrate(instance, null, targetTypes);
    }

    @Override
    public <T, M> M migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType) {
        return migrate(instance, migrationStrategy, targetType, new Class<?>[0]).as(targetType);
    }

    @Override
    public <T, M> M migrate(T instance, Class<M> targetType) {
        return migrate(instance, null, targetType);
    }

    @Override
    public <T> void delete(T instance) {
        Node node = instanceManager.getEntity(instance);
        instanceManager.removeInstance(instance);
        instanceManager.destroyInstance(instance);
        node.delete();
    }

    @Override
    public <QL> Query createQuery(QL query, Class<?>... types) {
        if (query instanceof String) {
            return new CypherStringQueryImpl(String.class.cast(query), datastoreSession, instanceManager, Arrays.asList(types));
        } else if (query instanceof Class<?>) {
            return new CypherTypeQueryImpl(Class.class.cast(query), datastoreSession, instanceManager, Arrays.asList(types));
        }
        throw new CdoException("Unsupported query language of type " + query.getClass().getName());
    }

    @Override
    public void close() {
        instanceManager.close();
    }

    @Override
    public <DS> DS getDatastoreSession(Class<DS> sessionType) {
        return sessionType.cast(datastoreSession);
    }

    private List<Class<?>> getEffectiveTypes(Class<?> type, Class<?>... types) {
        List<Class<?>> effectiveTypes = new ArrayList<>(types.length + 1);
        effectiveTypes.add(type);
        effectiveTypes.addAll(Arrays.asList(types));
        return effectiveTypes;
    }
}
