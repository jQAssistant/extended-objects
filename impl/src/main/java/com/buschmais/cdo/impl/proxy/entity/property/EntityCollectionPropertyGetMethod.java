package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.EntityPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.collection.AbstractCollectionProxy;
import com.buschmais.cdo.impl.proxy.collection.EntityCollectionProxy;
import com.buschmais.cdo.spi.metadata.method.EntityCollectionPropertyMethodMetadata;

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
    public EntityCollectionPropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, EntityCollectionPropertyMethodMetadata<?> metadata) {
        super(sessionContext, sessionContext.getEntityPropertyManager(), metadata);
    }

    @Override
    protected AbstractCollectionProxy<?, ?, ?, ?> createCollectionProxy(Entity entity, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        return new EntityCollectionProxy<>(sessionContext, entity, getMetadata());
    }
}
