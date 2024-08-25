package com.buschmais.xo.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.type.CompositeTypeMetadata;
import com.buschmais.xo.impl.proxy.relation.RelationProxyMethodService;
import com.buschmais.xo.spi.datastore.DatastoreSession;

/**
 * Implementation of an instance manager for relation types.
 */
public class RelationInstanceManager<Entity, EntityDiscriminator, RelationId, Relation, RelationDiscriminator>
    extends AbstractInstanceManager<RelationId, Relation> {

    private final SessionContext<?, Entity, ?, EntityDiscriminator, RelationId, Relation, ?, RelationDiscriminator, ?> sessionContext;

    public RelationInstanceManager(SessionContext<?, Entity, ?, EntityDiscriminator, RelationId, Relation, ?, RelationDiscriminator, ?> sessionContext) {
        super(sessionContext.getRelationCache(), sessionContext.getInstanceListenerService(), sessionContext.getProxyFactory(),
            new RelationProxyMethodService<>(sessionContext));
        this.sessionContext = sessionContext;
    }

    @Override
    public boolean isDatastoreType(Object o) {
        return sessionContext.getDatastoreSession()
            .getDatastoreRelationManager()
            .isRelation(o);
    }

    @Override
    public RelationId getDatastoreId(Relation relation) {
        if (relation == null) {
            throw new XOException("No relation provided.");
        }
        return sessionContext.getDatastoreSession()
            .getDatastoreRelationManager()
            .getRelationId(relation);
    }

    @Override
    protected CompositeTypeMetadata<?> getTypes(Relation relation) {
        DatastoreSession<?, Entity, ?, EntityDiscriminator, RelationId, Relation, ?, RelationDiscriminator, ?> datastoreSession = sessionContext.getDatastoreSession();
        Entity source = datastoreSession.getDatastoreRelationManager()
            .getFrom(relation);
        Entity target = datastoreSession.getDatastoreRelationManager()
            .getTo(relation);
        RelationDiscriminator discriminator = datastoreSession.getDatastoreRelationManager()
            .getRelationDiscriminator(relation);
        if (discriminator == null) {
            throw new XOException("Cannot determine type discriminators for relation '" + relation + "'");
        }
        return sessionContext.getMetadataProvider()
            .getRelationTypes(datastoreSession.getDatastoreEntityManager()
                .getEntityDiscriminators(source), discriminator, datastoreSession.getDatastoreEntityManager()
                .getEntityDiscriminators(target));
    }

}
