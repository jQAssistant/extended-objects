package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.impl.validation.InstanceValidator;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class CacheSynchronizationService<Entity, Relation> {

    private TransactionalCache<?> entityCache;
    private AbstractInstanceManager<?, Entity> entityInstanceManager;
    private TransactionalCache<?> relationCache;
    private AbstractInstanceManager<?, Relation> relationInstanceManager;
    private InstanceValidator instanceValidator;
    private DatastoreSession<?, Entity, ?, ?, ?, Relation, ?, ?> datastoreSession;

    public CacheSynchronizationService(TransactionalCache<?> entityCache, AbstractInstanceManager<?, Entity> entityInstanceManager, TransactionalCache<?> relationCache, AbstractInstanceManager<?, Relation> relationInstanceManager, InstanceValidator instanceValidator, DatastoreSession<?, Entity, ?, ?, ?, Relation, ?, ?> datastoreSession) {
        this.entityCache = entityCache;
        this.entityInstanceManager = entityInstanceManager;
        this.relationCache = relationCache;
        this.relationInstanceManager = relationInstanceManager;
        this.instanceValidator = instanceValidator;
        this.datastoreSession = datastoreSession;
    }

    public void flush() {
        Set<ConstraintViolation<Object>> constraintViolations = instanceValidator.validate();
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        for (Object instance : relationCache.readInstances()) {
            Relation relation = relationInstanceManager.getDatastoreType(instance);
            datastoreSession.flushRelation(relation);
        }
        for (Object instance : entityCache.readInstances()) {
            Entity entity = entityInstanceManager.getDatastoreType(instance);
            datastoreSession.flushEntity(entity);
        }
    }
}
