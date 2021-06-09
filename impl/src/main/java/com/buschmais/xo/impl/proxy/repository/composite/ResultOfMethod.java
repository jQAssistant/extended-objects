package com.buschmais.xo.impl.proxy.repository.composite;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.resultof.AbstractResultOfMethod;
import com.buschmais.xo.api.metadata.method.ResultOfMethodMetadata;

/**
 * Implementation of a result of method for repositories.
 *
 * @param <Entity>
 *            The entity type.
 * @param <Relation>
 *            The relation type.
 */
public class ResultOfMethod<T, Entity, Relation> extends AbstractResultOfMethod<T, Entity, Relation> {

    public ResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        super(sessionContext, resultOfMethodMetadata);
    }

    @Override
    protected Object getThisInstance(T datastoreType, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        return null;
    }

}
