package com.buschmais.xo.impl;

import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

/**
 * Contains methods for reading and creating relationships specified by the
 * given metadata.
 * <p/>
 * <p>
 * For each provided method the direction of the relationships is handled
 * transparently for the caller.
 * </p>
 */
public abstract class AbstractPropertyManager<DatastoreType, Entity, Relation> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    /**
     * Constructor.
     *
     * @param sessionContext The {@link com.buschmais.xo.impl.SessionContext}.
     */
    public AbstractPropertyManager(
            SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    protected SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> getSessionContext() {
        return sessionContext;
    }

    /**
     * Get the target node of a single relationship.
     *
     * @param source The source entity.
     * @return The target node or <code>null</code>.
     */
    protected Relation getSingleRelation(Entity source,
                                         RelationTypeMetadata metadata,
                                         RelationTypeMetadata.Direction direction) {
        DatastoreRelationManager<Entity, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?, ?> relationManager = sessionContext.getDatastoreSession().getDatastoreRelationManager();
        if (relationManager.hasSingleRelation(source, metadata,
                direction)) {
            return relationManager.getSingleRelation(source, metadata,
                    direction);
        }
        return null;
    }

    public abstract void setProperty(DatastoreType datastoreType,
                                     PrimitivePropertyMethodMetadata metadata, Object value);

    public abstract Object getProperty(DatastoreType datastoreType,
                                       PrimitivePropertyMethodMetadata metadata);

    public abstract boolean hasProperty(DatastoreType datastoreType,
                                        PrimitivePropertyMethodMetadata metadata);

    public abstract void removeProperty(DatastoreType datastoreType,
                                        PrimitivePropertyMethodMetadata metadata);
}
