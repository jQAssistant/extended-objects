package com.buschmais.xo.impl.proxy.entity.resultof;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.resultof.AbstractResultOfMethod;
import com.buschmais.xo.spi.metadata.method.ResultOfMethodMetadata;

/**
 * Implementation of a result of method for relations.
 *
 * @param <Entity>
 *            The entity type.
 * @param <Relation>
 *            The relation type.
 */
public class ResultOfMethod<Entity, Relation> extends AbstractResultOfMethod<Entity, Entity, Relation> {

    public ResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        super(sessionContext, resultOfMethodMetadata);
    }

    @Override
    protected Object getThisInstance(Entity datastoreType, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        return sessionContext.getEntityInstanceManager().readInstance(datastoreType);
    }

}
