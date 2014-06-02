package com.buschmais.xo.impl;

import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.metadata.method.EntityReferencePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

/**
 * Contains methods for reading and creating relationships specified by the given metadata.
 * <p>For each provided method the direction of the relationships is handled transparently for the caller.</p>
 */
public class RelationPropertyManager<Entity, Relation> extends AbstractPropertyManager<Relation, Entity, Relation> {

    /**
     * Constructor.
     *
     * @param sessionContext The {@link SessionContext}.
     */
    public RelationPropertyManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        super(sessionContext);
    }

    @Override
    protected DatastorePropertyManager<Relation, ?> getPropertyManager() {
        return getSessionContext().getDatastoreSession().getDatastoreRelationManager();
    }

    @Override
    protected AbstractInstanceManager<?, Relation> getInstanceManager() {
        return getSessionContext().getRelationInstanceManager();
    }

    public Entity getEntityReference(Relation relation, EntityReferencePropertyMethodMetadata metadata) {
        return getSessionContext().getEntityInstanceManager().readInstance(getReferencedEntity(relation, metadata.getDirection()));
    }

    private Entity getReferencedEntity(Relation relation, RelationTypeMetadata.Direction direction) {
        DatastoreRelationManager<Entity, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> relationManager = getSessionContext().getDatastoreSession().getDatastoreRelationManager();
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
