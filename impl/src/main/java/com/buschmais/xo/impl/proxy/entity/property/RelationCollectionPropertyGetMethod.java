package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.api.metadata.method.RelationCollectionPropertyMethodMetadata;
import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.collection.AbstractCollectionProxy;
import com.buschmais.xo.impl.proxy.collection.RelationCollectionProxy;

/**
 * Get method for relation collections.
 *
 * @param <Entity>
 *     The entity type.
 * @param <Relation>
 *     The relation type.
 */
public class RelationCollectionPropertyGetMethod<Entity, Relation> extends
    AbstractCollectionPropertyGetMethod<Entity, Entity, Relation, EntityPropertyManager<Entity, Relation, ?>, RelationCollectionPropertyMethodMetadata<?>> {

    /**
     * Constructor.
     *
     * @param sessionContext
     *     The session context.
     * @param metadata
     *     The metadata.
     */
    public RelationCollectionPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext,
        RelationCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, sessionContext.getEntityPropertyManager(), metadata);
    }

    @Override
    protected AbstractCollectionProxy<?, ?, ?, ?> createCollectionProxy(Entity entity, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        return new RelationCollectionProxy<>(sessionContext, entity, getMetadata());
    }

}
