package com.buschmais.xo.impl.proxy.relation.resultof;

import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.resultof.AbstractResultOfMethod;
import com.buschmais.xo.spi.metadata.method.ResultOfMethodMetadata;

public class ResultOfMethod<Entity, Relation> extends AbstractResultOfMethod<Relation, Entity, Relation> {

    public ResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        super(sessionContext, resultOfMethodMetadata);
    }

    @Override
    protected AbstractInstanceManager<?, Relation> getInstanceManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        return sessionContext.getRelationInstanceManager();
    }
}
