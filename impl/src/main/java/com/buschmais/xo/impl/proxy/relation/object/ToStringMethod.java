package com.buschmais.xo.impl.proxy.relation.object;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.object.AbstractDatastoreTypeToStringMethod;
import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class ToStringMethod<Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator>
        extends AbstractDatastoreTypeToStringMethod<Relation> {

    private final SessionContext<?, Entity, EntityMetadata, EntityDiscriminator, ?, Relation, RelationMetadata, RelationDiscriminator, ?> sessionContext;

    public ToStringMethod(
            SessionContext<?, Entity, EntityMetadata, EntityDiscriminator, ?, Relation, RelationMetadata, RelationDiscriminator, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected String getId(Relation datastoreType) {
        return sessionContext.getDatastoreSession().getDatastoreRelationManager().getRelationId(datastoreType).toString();
    }

    @Override
    protected Object getProperty(Relation datastoreType, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        return sessionContext.getDatastoreSession().getDatastoreRelationManager().getProperty(datastoreType, propertyMethodMetadata);
    }

    @Override
    protected TypeMetadataSet<?> getTypes(Relation datastoreType) {
        DatastoreRelationManager<Entity, ?, Relation, RelationMetadata, RelationDiscriminator, ?> datastoreRelationManager = sessionContext
                .getDatastoreSession().getDatastoreRelationManager();
        DatastoreEntityManager<?, Entity, EntityMetadata, EntityDiscriminator, ?> datastoreEntityManager = sessionContext.getDatastoreSession()
                .getDatastoreEntityManager();
        Entity from = datastoreRelationManager.getFrom(datastoreType);
        Entity to = datastoreRelationManager.getTo(datastoreType);
        return sessionContext.getMetadataProvider().getRelationTypes(datastoreEntityManager.getEntityDiscriminators(from),
                datastoreRelationManager.getRelationDiscriminator(datastoreType), datastoreEntityManager.getEntityDiscriminators(to));
    }
}
