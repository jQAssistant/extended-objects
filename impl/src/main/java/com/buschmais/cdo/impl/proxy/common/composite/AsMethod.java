package com.buschmais.cdo.impl.proxy.common.composite;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.SessionContext;

import java.util.Map;

public class AsMethod<DatastoreType, Entity, Relation> implements ProxyMethod<DatastoreType> {

    private SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;

    public AsMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Class<?> targetType = (Class<?>) args[0];
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (targetType.isAssignableFrom(type)) {
                if (sessionContext.getEntityInstanceManager().isInstance(instance)) {
                    return sessionContext.getEntityInstanceManager().getInstance((Entity) datastoreType);
                } else if (sessionContext.getRelationInstanceManager().isInstance(instance)) {
                    return sessionContext.getRelationInstanceManager().getInstance((Relation) instance);
                }
            }
        }
        if (datastoreType instanceof Map) {
            Map map = (Map) datastoreType;
            if (map.size() == 1) {
                return map.values().iterator().next();
            }
        }
        throw new CdoException(instance + " cannot be cast to " + targetType.getName());
    }
}
