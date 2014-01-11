package com.buschmais.cdo.impl.proxy.common.composite;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;

import java.util.Map;

public class AsMethod<DatastoreType> implements ProxyMethod<DatastoreType> {

    private InstanceManager instanceManager;

    public AsMethod(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Class<?> targetType = (Class<?>) args[0];
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (targetType.isAssignableFrom(type)) {
                if (instanceManager.isEntity(instance)) {
                    return instanceManager.getEntityInstance(datastoreType);
                } else if (instanceManager.isRelation(instance)) {
                    return instanceManager.getRelation(instance);
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
