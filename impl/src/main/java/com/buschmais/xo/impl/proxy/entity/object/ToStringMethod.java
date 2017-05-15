package com.buschmais.xo.impl.proxy.entity.object;

import java.util.Set;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.object.AbstractDatastoreTypeToStringMethod;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

public class ToStringMethod<Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator>
        extends AbstractDatastoreTypeToStringMethod<Entity> {

    private final SessionContext<?, Entity, EntityMetadata, EntityDiscriminator, ?, ?, ?, ?, ?> sessionContext;

    public ToStringMethod(SessionContext<?, Entity, EntityMetadata, EntityDiscriminator, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    protected String getId(Entity datastoreType) {
        return sessionContext.getDatastoreSession().getDatastoreEntityManager().getEntityId(datastoreType).toString();
    }

    @Override
    protected Object getProperty(Entity datastoreType, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        return sessionContext.getDatastoreSession().getDatastoreEntityManager().getProperty(datastoreType, propertyMethodMetadata);
    }

    protected TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getTypes(Entity entity) {
        Set<EntityDiscriminator> discriminators = sessionContext.getDatastoreSession().getDatastoreEntityManager().getEntityDiscriminators(entity);
        return sessionContext.getMetadataProvider().getTypes(discriminators);
    }
}
