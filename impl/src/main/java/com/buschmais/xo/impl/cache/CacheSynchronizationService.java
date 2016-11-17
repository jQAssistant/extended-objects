package com.buschmais.xo.impl.cache;

import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.instancelistener.InstanceListenerService;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class CacheSynchronizationService<Entity, Relation> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;
    private ValidationMode validationMode;

    public CacheSynchronizationService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, ValidationMode validationMode) {
        this.sessionContext = sessionContext;
        this.validationMode = validationMode;
    }

    public void flush() {
        DatastoreSession<?, Entity, ? extends DatastoreEntityMetadata<?>, ?, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> datastoreSession = sessionContext.getDatastoreSession();
        InstanceListenerService instanceListenerService = sessionContext.getInstanceListenerService();
        for (Object instance : sessionContext.getRelationCache().writtenInstances()) {
            Relation relation = sessionContext.getRelationInstanceManager().getDatastoreType(instance);
            instanceListenerService.preUpdate(instance);
            validateInstance(instance);
            datastoreSession.getDatastoreRelationManager().flushRelation(relation);
            instanceListenerService.postUpdate(instance);
        }
        for (Object instance : sessionContext.getEntityCache().writtenInstances()) {
            Entity entity = sessionContext.getEntityInstanceManager().getDatastoreType(instance);
            instanceListenerService.preUpdate(instance);
            validateInstance(instance);
            datastoreSession.getDatastoreEntityManager().flushEntity(entity);
            instanceListenerService.postUpdate(instance);
        }
    }

    public void clear() {
        DatastoreSession<?, Entity, ? extends DatastoreEntityMetadata<?>, ?, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> datastoreSession = sessionContext.getDatastoreSession();
        for (Object instance : sessionContext.getRelationCache().writtenInstances()) {
            Relation relation = sessionContext.getRelationInstanceManager().getDatastoreType(instance);
            datastoreSession.getDatastoreRelationManager().clearRelation(relation);
        }
        for (Object instance : sessionContext.getEntityCache().writtenInstances()) {
            Entity entity = sessionContext.getEntityInstanceManager().getDatastoreType(instance);
            datastoreSession.getDatastoreEntityManager().clearEntity(entity);
        }
    }

    private void validateInstance(Object instance) {
        if (!ValidationMode.NONE.equals(validationMode)) {
            Set<ConstraintViolation<Object>> constraintViolations = sessionContext.getInstanceValidationService().validate(instance);
            if (!constraintViolations.isEmpty()) {
                throw new ConstraintViolationException(constraintViolations);
            }
        }
    }
}
