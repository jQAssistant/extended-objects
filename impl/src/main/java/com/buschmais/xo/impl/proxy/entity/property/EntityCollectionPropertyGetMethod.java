package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.collection.AbstractCollectionProxy;
import com.buschmais.xo.impl.proxy.collection.EntityCollectionProxy;
import com.buschmais.xo.spi.metadata.method.EntityCollectionPropertyMethodMetadata;

/**
 * Get method for entity collections.
 *
 * @param <Entity>   The entity type.
 * @param <Relation> The relation type.
 */
public class EntityCollectionPropertyGetMethod<Entity, Relation> extends AbstractCollectionPropertyGetMethod<Entity, Entity, Relation, EntityPropertyManager<Entity, Relation>, EntityCollectionPropertyMethodMetadata<?>> {

    /**
     * Constructor.
     *
     * @param sessionContext The session context.
     * @param metadata       The metadata.
     */
    public EntityCollectionPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, EntityCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, sessionContext.getEntityPropertyManager(), metadata);
    }

    @Override
    protected AbstractCollectionProxy<?, ?, ?, ?> createCollectionProxy(Entity entity, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        return new EntityCollectionProxy<>(sessionContext, entity, getMetadata());
    }
}
