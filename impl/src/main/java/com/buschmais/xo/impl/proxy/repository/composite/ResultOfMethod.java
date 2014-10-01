package com.buschmais.xo.impl.proxy.repository.composite;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.resultof.AbstractResultOfMethod;
import com.buschmais.xo.spi.metadata.method.ResultOfMethodMetadata;

public class ResultOfMethod<Entity, Relation> extends AbstractResultOfMethod<Entity, Relation, XOManager> {

    public ResultOfMethod(SessionContext sessionContext, ResultOfMethodMetadata resultOfMethodMetadata) {
        super(sessionContext, resultOfMethodMetadata);
    }

    @Override
    protected AbstractInstanceManager<?, XOManager> getInstanceManager(SessionContext sessionContext) {
        return null;
    }
}
