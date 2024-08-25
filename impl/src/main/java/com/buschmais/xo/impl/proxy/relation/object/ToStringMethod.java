package com.buschmais.xo.impl.proxy.relation.object;

import com.buschmais.xo.api.metadata.type.CompositeTypeMetadata;
import com.buschmais.xo.api.metadata.type.DatastoreEntityMetadata;
import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.object.AbstractDatastoreTypeToStringMethod;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;

public class ToStringMethod<Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator>
    extends AbstractDatastoreTypeToStringMethod<Relation> {

    private final SessionContext<?, Entity, EntityMetadata, EntityDiscriminator, ?, Relation, RelationMetadata, RelationDiscriminator, ?> sessionContext;

    private final DatastoreEntityManager<?, Entity, EntityMetadata, EntityDiscriminator, ?> datastoreEntityManager;
    private final DatastoreRelationManager<Entity, ?, Relation, RelationMetadata, RelationDiscriminator, ?> datastoreRelationManager;

    public ToStringMethod(
        SessionContext<?, Entity, EntityMetadata, EntityDiscriminator, ?, Relation, RelationMetadata, RelationDiscriminator, ?> sessionContext) {
        this.sessionContext = sessionContext;
        this.datastoreEntityManager = sessionContext.getDatastoreSession()
            .getDatastoreEntityManager();
        this.datastoreRelationManager = sessionContext.getDatastoreSession()
            .getDatastoreRelationManager();
    }

    @Override
    protected String getId(Relation datastoreType) {
        return datastoreRelationManager.getRelationId(datastoreType)
            .toString();
    }

    @Override
    protected CompositeTypeMetadata<?> getDynamicType(Relation datastoreType) {
        Entity from = datastoreRelationManager.getFrom(datastoreType);
        Entity to = datastoreRelationManager.getTo(datastoreType);
        return sessionContext.getMetadataProvider()
            .getRelationTypes(datastoreEntityManager.getEntityDiscriminators(from), datastoreRelationManager.getRelationDiscriminator(datastoreType),
                datastoreEntityManager.getEntityDiscriminators(to));
    }

    @Override
    protected DatastorePropertyManager<Relation, ?> getDatastorePropertyManager() {
        return datastoreRelationManager;
    }
}
