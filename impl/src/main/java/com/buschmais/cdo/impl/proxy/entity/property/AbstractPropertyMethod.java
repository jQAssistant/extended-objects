package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.AbstractMethodMetadata;

public abstract class AbstractPropertyMethod<Entity, Relation, M extends AbstractMethodMetadata> implements ProxyMethod<Entity> {

    private SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;

    private M metadata;

    protected AbstractPropertyMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, M metadata) {
        this.sessionContext = sessionContext;
        this.metadata = metadata;
    }

    public SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> getSessionContext() {
        return sessionContext;
    }

    protected M getMetadata() {
        return metadata;
    }

}
