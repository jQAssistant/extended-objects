package com.buschmais.xo.impl.proxy.entity.resultof;

import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.resultof.AbstractResultOfMethod;
import com.buschmais.xo.spi.metadata.method.ResultOfMethodMetadata;

public class ResultOfMethod<Entity, Relation> extends AbstractResultOfMethod<Entity, Entity, Relation> {

    public ResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        super(sessionContext, resultOfMethodMetadata);
    }

    @Override
    protected AbstractInstanceManager<?, Entity> getInstanceManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        return sessionContext.getEntityInstanceManager();
    }
}
