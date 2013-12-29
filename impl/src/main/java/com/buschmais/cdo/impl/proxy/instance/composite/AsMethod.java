package com.buschmais.cdo.impl.proxy.instance.composite;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;

import java.util.Map;

public class AsMethod<Entity> implements ProxyMethod<Entity> {

    private final InstanceManager instanceManager;

    public AsMethod(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Class<?> targetType = (Class<?>) args[0];
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (targetType.isAssignableFrom(type)) {
                return instanceManager.getInstance(entity);
            }
        }
        if (entity instanceof Map) {
            Map map = (Map) entity;
            if (map.size() == 1) {
                return map.values().iterator().next();
            }
        }
        throw new CdoException(instance + " cannot be cast to " + targetType.getName());
    }
}
