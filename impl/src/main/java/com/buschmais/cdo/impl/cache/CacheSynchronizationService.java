package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.instancelistener.InstanceListenerService;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class CacheSynchronizationService<Entity, Relation> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;

    public CacheSynchronizationService(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    public void flush() {
        Set<ConstraintViolation<Object>> constraintViolations = sessionContext.getInstanceValidator().validate();
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        DatastoreSession<?, Entity, ? extends DatastoreEntityMetadata<?>, ?, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?> datastoreSession = sessionContext.getDatastoreSession();
        InstanceListenerService instanceListenerService = sessionContext.getInstanceListenerService();
        for (Object instance : sessionContext.getRelationCache().readInstances()) {
            Relation relation = sessionContext.getRelationInstanceManager().getDatastoreType(instance);
            instanceListenerService.preUpdate(instance);
            datastoreSession.flushRelation(relation);
            instanceListenerService.postUpdate(instance);
        }
        for (Object instance : sessionContext.getEntityCache().readInstances()) {
            Entity entity = sessionContext.getEntityInstanceManager().getDatastoreType(instance);
            instanceListenerService.preUpdate(instance);
            datastoreSession.flushEntity(entity);
            instanceListenerService.postUpdate(instance);
        }
    }
}
