package com.buschmais.cdo.impl.proxy.entity.resultof;

import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.resultof.AbstractResultOfMethod;
import com.buschmais.cdo.spi.metadata.method.ResultOfMethodMetadata;

public class ResultOfMethod<Entity, Relation> extends AbstractResultOfMethod<Entity, Entity, Relation> {

    public ResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        super(sessionContext, resultOfMethodMetadata);
    }

    @Override
    protected AbstractInstanceManager<?, Entity> getInstanceManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        return sessionContext.getEntityInstanceManager();
    }
}