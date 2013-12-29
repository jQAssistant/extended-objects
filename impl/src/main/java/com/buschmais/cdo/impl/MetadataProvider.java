package com.buschmais.cdo.impl;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.EntityTypeMetadata;
import com.buschmais.cdo.spi.metadata.RelationTypeMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Defines the interface for the metadata provider.
 *
 * @param <EntityMetadata>        The type of datastore specific entity metadata.
 * @param <EntityDiscriminator>   The type of datastore specific entity type discriminators.
 * @param <RelationMetadata>      The type of datastore specific relation metadata.
 * @param <RelationDiscriminator> The type of datastore specific relationtype discriminators.
 */
public interface MetadataProvider<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    /**
     * Determine the {@link TypeMetadataSet} for a given set of entity discriminators.
     *
     * @param entityDiscriminators The entity descriminators.
     * @return The {@link TypeMetadataSet}.
     */
    TypeMetadataSet<EntityMetadata> getTypes(Set<EntityDiscriminator> entityDiscriminators);

    /**
     * Determine the set of entity discriminators for the given {@link TypeMetadataSet}.
     *
     * @param types The {@link TypeMetadataSet}.
     * @return The set of discriminators.
     */
    Set<EntityDiscriminator> getDiscriminators(TypeMetadataSet<EntityMetadata> types);

    /**
     * Return a collection of all registered entity type metadata.
     *
     * @return The collection of all registered entity type metadata.
     */
    Collection<EntityTypeMetadata<EntityMetadata>> getRegisteredMetadata();

    /**
     * Return the entity metadata for a specific type.
     *
     * @param type The type.
     * @return The entity metadata.
     */
    EntityTypeMetadata<EntityMetadata> getEntityMetadata(Class<?> type);

    RelationTypeMetadata<RelationMetadata> getRelationMetadata(Class<?> relationType);

    RelationTypeMetadata.Direction getRelationDirection(List<Class<?>> sourceTypes, RelationTypeMetadata<RelationTypeMetadata> relationMetadata, List<Class<?>> targetTypes);
}
