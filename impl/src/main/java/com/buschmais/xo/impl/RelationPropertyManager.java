package com.buschmais.xo.impl;

import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;
import com.buschmais.xo.api.metadata.method.EntityReferencePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;

public class RelationPropertyManager<Entity, Relation> extends AbstractPropertyManager<Relation> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    /**
     * Constructor.
     *
     * @param sessionContext
     *            The {@link SessionContext}.
     */
    public RelationPropertyManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected DatastorePropertyManager<Relation, ?> getDatastorePropertyManager() {
        return sessionContext.getDatastoreSession().getDatastoreRelationManager();
    }

    @Override
    protected AbstractInstanceManager<?, Relation> getInstanceManager() {
        return sessionContext.getRelationInstanceManager();
    }

    public Entity getEntityReference(Relation relation, EntityReferencePropertyMethodMetadata metadata) {
        return sessionContext.getEntityInstanceManager().readInstance(getReferencedEntity(relation, metadata.getDirection()));
    }

    private Entity getReferencedEntity(Relation relation, RelationTypeMetadata.Direction direction) {
        DatastoreRelationManager<Entity, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> relationManager = sessionContext.getDatastoreSession()
                .getDatastoreRelationManager();
        switch (direction) {
        case TO:
            return relationManager.getTo(relation);
        case FROM:
            return relationManager.getFrom(relation);
        default:
            throw direction.createNotSupportedException();
        }
    }

}
