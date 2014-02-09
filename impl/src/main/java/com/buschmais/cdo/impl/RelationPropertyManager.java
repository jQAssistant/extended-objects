package com.buschmais.cdo.impl;

import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.metadata.method.EntityReferencePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

/**
 * Contains methods for reading and creating relationships specified by the given metadata.
 * <p/>
 * <p>For each provided method the direction of the relationships is handled transparently for the caller.</p>
 */
public class RelationPropertyManager<Entity, Relation> extends AbstractPropertyManager<Relation, Entity, Relation> {

    /**
     * Constructor.
     *
     * @param sessionContext The {@link SessionContext}.
     */
    public RelationPropertyManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext) {
        super(sessionContext);
    }

    @Override
    public void setProperty(Relation relation, PrimitivePropertyMethodMetadata metadata, Object value) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        sessionContext.getDatastoreSession().getDatastorePropertyManager().setRelationProperty(relation, metadata, value);
        sessionContext.getRelationInstanceManager().writeInstance(relation);
    }

    @Override
    public Object getProperty(Relation relation, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastorePropertyManager().getRelationProperty(relation, metadata);
    }

    @Override
    public boolean hasProperty(Relation relation, PrimitivePropertyMethodMetadata metadata) {
        return getSessionContext().getDatastoreSession().getDatastorePropertyManager().hasRelationProperty(relation, metadata);
    }

    @Override
    public void removeProperty(Relation relation, PrimitivePropertyMethodMetadata metadata) {
        SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext = getSessionContext();
        sessionContext.getDatastoreSession().getDatastorePropertyManager().removeRelationProperty(relation, metadata);
        sessionContext.getRelationInstanceManager().writeInstance(relation);
    }

    public Entity getEntityReference(Relation relation, EntityReferencePropertyMethodMetadata metadata) {
        return getSessionContext().getEntityInstanceManager().readInstance(getReferencedEntity(relation, metadata.getDirection()));
    }

    private Entity getReferencedEntity(Relation relation, RelationTypeMetadata.Direction direction) {
        DatastorePropertyManager<Entity, Relation, ?, ?, ? extends DatastoreRelationMetadata<?>> datastorePropertyManager = getSessionContext().getDatastoreSession().getDatastorePropertyManager();
        switch (direction) {
            case TO:
                return datastorePropertyManager.getTo(relation);
            case FROM:
                return datastorePropertyManager.getFrom(relation);
            default:
                throw direction.createNotSupportedException();
        }
    }
}
