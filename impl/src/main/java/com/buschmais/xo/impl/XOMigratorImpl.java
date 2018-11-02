package com.buschmais.xo.impl;

import java.util.HashSet;
import java.util.Set;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOMigrator;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

/**
 * Implementation of the {@link com.buschmais.xo.api.XOMigrator} interface.
 * 
 * @param <EntityId>
 *            The type of entity ids as provided by the datastore.
 * @param <Entity>
 *            The type of entities as provided by the datastore.
 * @param <EntityMetadata>
 *            The type of entity metadata as provided by the datastore.
 * @param <EntityDiscriminator>
 *            The type of discriminators as provided by the datastore.
 */
public class XOMigratorImpl<T, EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator>
		implements XOMigrator<T> {

	private T instance;

	private MetadataProvider<EntityMetadata, EntityDiscriminator, ? extends DatastoreRelationMetadata<?>, ?> metadataProvider;
	private DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?> datastoreEntityManager;
	private AbstractInstanceManager<EntityId, Entity> entityInstanceManager;

	/**
	 * Constructor.
	 * 
	 * @param instance
	 *            The instance to migrate.
	 * @param sessionContext
	 *            The session context.
	 */
	XOMigratorImpl(T instance, SessionContext<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?, ?, ?, ?, ?> sessionContext) {
		this.instance = instance;
		metadataProvider = sessionContext.getMetadataProvider();
		this.datastoreEntityManager = sessionContext.getDatastoreSession().getDatastoreEntityManager();
		this.entityInstanceManager = sessionContext.getEntityInstanceManager();
	}

	@Override
	public CompositeObject add(Class<?> newType, Class<?>... newTypes) {
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types = getDiscriminators(newType, newTypes);
        Set<EntityDiscriminator> newDiscriminators = new HashSet<>(metadataProvider.getEntityDiscriminators(types));
		Entity entity = removeInstance(entityInstanceManager);
		Set<EntityDiscriminator> entityDiscriminators = datastoreEntityManager.getEntityDiscriminators(entity);
		newDiscriminators.removeAll(entityDiscriminators);
		datastoreEntityManager.addDiscriminators(types, entity, newDiscriminators);
		return createInstance(entity);
	}

	@Override
	public CompositeObject remove(Class<?> obsoleteType, Class<?>... obsoleteTypes) {
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types = getDiscriminators(obsoleteType, obsoleteTypes);
        Set<EntityDiscriminator> obsoleteDiscriminators = new HashSet<>(metadataProvider.getEntityDiscriminators(types));
		Entity entity = removeInstance(entityInstanceManager);
		Set<EntityDiscriminator> entityDiscriminators = datastoreEntityManager.getEntityDiscriminators(entity);
		obsoleteDiscriminators.retainAll(entityDiscriminators);
		datastoreEntityManager.removeDiscriminators(types, entity, obsoleteDiscriminators);
		return createInstance(entity);
	}

	private TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getDiscriminators(Class<?> type, Class<?>[] types) {
		TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> typeMetadata = new TypeMetadataSet<>();
		typeMetadata.add(metadataProvider.getEntityMetadata(type));
		for (Class<?> currentType : types) {
			typeMetadata.add(metadataProvider.getEntityMetadata(currentType));
		}
		return typeMetadata;
	}

	private Entity removeInstance(AbstractInstanceManager<EntityId, Entity> entityInstanceManager) {
		Entity entity = entityInstanceManager.getDatastoreType(instance);
		entityInstanceManager.removeInstance(instance);
		return entity;
	}

	private CompositeObject createInstance(Entity entity) {
		instance = entityInstanceManager.createInstance(entity);
		return CompositeObject.class.cast(instance);
	}

}
