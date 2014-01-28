package com.buschmais.cdo.impl.proxy.common.property;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.AbstractMethodMetadata;

public abstract class AbstractPropertyMethod<DatastoreType, Entity, Relation, M extends AbstractMethodMetadata> implements ProxyMethod<DatastoreType> {

    private SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;

    private M metadata;

    protected AbstractPropertyMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, M metadata) {
        this.sessionContext = sessionContext;
        this.metadata = metadata;
    }

    protected SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> getSessionContext() {
        return sessionContext;
    }

    protected M getMetadata() {
        return metadata;
    }

    protected abstract AbstractPropertyManager<DatastoreType, Entity, Relation> getPropertyManager();

}
