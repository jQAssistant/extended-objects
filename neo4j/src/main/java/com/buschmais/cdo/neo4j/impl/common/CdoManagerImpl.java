package com.buschmais.cdo.neo4j.impl.common;

import com.buschmais.cdo.api.*;
import com.buschmais.cdo.neo4j.impl.query.CypherStringQueryImpl;
import com.buschmais.cdo.neo4j.impl.query.CypherTypeQueryImpl;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.neo4j.spi.TypeSet;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import javax.validation.ConstraintViolation;
import java.util.*;

public class CdoManagerImpl implements CdoManager {

    private final CdoTransaction cdoTransaction;
    private final DatastoreSession<Long, Node, Long, Relationship, ?, ?, ?> datastoreSession;
    private final InstanceManager<Long, Node> instanceManager;
    private final InstanceValidator instanceValidator;

    public CdoManagerImpl(CdoTransaction cdoTransaction, DatastoreSession<Long, Node, Long, Relationship, ?, ?, ?> datastoreSession, InstanceManager instanceManager, InstanceValidator instanceValidator) {
        this.cdoTransaction = cdoTransaction;
        this.datastoreSession = datastoreSession;
        this.instanceManager = instanceManager;
        this.instanceValidator = instanceValidator;
    }

    @Override
    public CdoTransaction currentTransaction() {
        return cdoTransaction;
    }

    @Override
    public Set<ConstraintViolation<Object>> validate() {
        return instanceValidator.validate();
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
        TypeSet effectiveTypes = getEffectiveTypes(type, types);
        Node node = datastoreSession.create(effectiveTypes);
        return instanceManager.getInstance(node);
    }

    public <T> T create(Class<T> type) {
        return create(type, new Class<?>[0]).as(type);
    }

    @Override
    public <T, M> CompositeObject migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType, Class<?>... targetTypes) {
        Node node = instanceManager.getEntity(instance);
        TypeSet types = datastoreSession.getTypes(node);
        TypeSet effectiveTargetTypes = getEffectiveTypes(targetType, targetTypes);
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
        datastoreSession.delete(node);
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

    private TypeSet getEffectiveTypes(Class<?> type, Class<?>... types) {
        TypeSet effectiveTypes = new TypeSet();
        effectiveTypes.add(type);
        effectiveTypes.addAll(Arrays.asList(types));
        return effectiveTypes;
    }
}
