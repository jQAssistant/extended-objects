package com.buschmais.xo.impl;

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
    public void setProperty(Relation relation, PrimitivePropertyMethodMetadata metadata, Object value) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext = getSessionContext();
        sessionContext.getDatastoreSession().getDatastoreRelationManager().setProperty(relation, metadata, value);
        sessionContext.getRelationInstanceManager().updateInstance(relation);
    }

    @Override
    public Object getProperty(Relation relation, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastoreRelationManager().getProperty(relation, metadata);
    }

    @Override
    public boolean hasProperty(Relation relation, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastoreRelationManager().hasProperty(relation, metadata);
    }

    @Override
    public void removeProperty(Relation relation, PrimitivePropertyMethodMetadata metadata) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext = getSessionContext();
        sessionContext.getDatastoreSession().getDatastoreRelationManager().removeProperty(relation, metadata);
        sessionContext.getRelationInstanceManager().updateInstance(relation);
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
