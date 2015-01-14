package com.buschmais.xo.impl;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

import java.util.HashSet;
import java.util.Set;

public class XOMigratorImpl<T, EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator> implements
        XOManager.XOMigrator<T> {

    private T instance;

    private SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?, ?, ?, ?, ?> sessionContext;

    XOMigratorImpl(T instance, SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?, ?, ?, ?, ?> sessionContext) {
        this.instance = instance;
        this.sessionContext = sessionContext;
    }

    @Override
    public CompositeObject add(Class<?> newType, Class<?>... newTypes) {
        Set<EntityDiscriminator> newDiscriminators = getDiscriminators(newType, newTypes);
        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        Entity entity = invalidateInstance(entityInstanceManager);
        DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?> datastoreEntityManager = sessionContext.getDatastoreSession()
                .getDatastoreEntityManager();
        Set<EntityDiscriminator> entityDiscriminators = datastoreEntityManager.getEntityDiscriminators(entity);
        newDiscriminators.removeAll(entityDiscriminators);
        datastoreEntityManager.addDiscriminators(entity, newDiscriminators);
        instance = entityInstanceManager.createInstance(entity);
        return CompositeObject.class.cast(instance);
    }

    @Override
    public CompositeObject remove(Class<?> obsoleteType, Class<?>... obsoleteTypes) {
        Set<EntityDiscriminator> obsoleteDiscriminators = getDiscriminators(obsoleteType, obsoleteTypes);
        AbstractInstanceManager<EntityId, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?> datastoreEntityManager = sessionContext.getDatastoreSession()
                .getDatastoreEntityManager();
        Entity entity = invalidateInstance(entityInstanceManager);
        Set<EntityDiscriminator> entityDiscriminators = datastoreEntityManager.getEntityDiscriminators(entity);
        obsoleteDiscriminators.retainAll(entityDiscriminators);
        datastoreEntityManager.removeDiscriminators(entity, obsoleteDiscriminators);
        instance = entityInstanceManager.createInstance(entity);
        return CompositeObject.class.cast(instance);
    }

    private Set<EntityDiscriminator> getDiscriminators(Class<?> type, Class<?>[] types) {
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> typeMetadata = new TypeMetadataSet<>();
        MetadataProvider<EntityMetadata, EntityDiscriminator, ? extends DatastoreRelationMetadata<?>, ?> metadataProvider = sessionContext
                .getMetadataProvider();
        typeMetadata.add(metadataProvider.getEntityMetadata(type));
        for (Class<?> currentType : types) {
            typeMetadata.add(metadataProvider.getEntityMetadata(currentType));
        }
        return new HashSet<>(metadataProvider.getEntityDiscriminators(typeMetadata));
    }

    private Entity invalidateInstance(AbstractInstanceManager<EntityId, Entity> entityInstanceManager) {
        Entity entity = entityInstanceManager.getDatastoreType(instance);
        entityInstanceManager.removeInstance(instance);
        entityInstanceManager.closeInstance(instance);
        return entity;
    }
}
