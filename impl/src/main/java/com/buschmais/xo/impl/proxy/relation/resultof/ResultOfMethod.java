package com.buschmais.xo.impl.proxy.relation.resultof;

import com.buschmais.xo.api.metadata.method.ResultOfMethodMetadata;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.resultof.AbstractResultOfMethod;

/**
 * Implementation of a result of method for entities.
 *
 * @param <Entity>
 *     The entity type.
 * @param <Relation>
 *     The relation type.
 */
public class ResultOfMethod<Entity, Relation> extends AbstractResultOfMethod<Relation, Entity, Relation> {

    public ResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        super(sessionContext, resultOfMethodMetadata);
    }

    @Override
    protected Object getThisInstance(Relation datastoreType, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        return sessionContext.getRelationInstanceManager()
            .readInstance(datastoreType);
    }
}
