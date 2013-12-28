package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.EntityTypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Allows resolving types from entity discriminators as provided by the datastores.
 *
 * @param <Discriminator> The discriminator type of the datastore (e.g. Neo4j labels or strings for JSON stores).
 */
public class EntityTypeMetadataResolver<EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeMetadataResolver.class);

    private Map<Class<?>, EntityTypeMetadata<EntityMetadata>> metadataByType;
    private Map<EntityTypeMetadata<EntityMetadata>, Set<Discriminator>> aggregatedDiscriminators = new HashMap<>();
    private Map<Discriminator, Set<EntityTypeMetadata<EntityMetadata>>> typeMetadataByDiscriminator = new HashMap<>();

    /**
     * Constructor.
     *
     * @param metadataByType A map of all types with their metadata.
     */
    public EntityTypeMetadataResolver(Map<Class<?>, EntityTypeMetadata<EntityMetadata>> metadataByType) {
        LOGGER.debug("Type metadata = '{}'", metadataByType);
        this.metadataByType = metadataByType;
        for (EntityTypeMetadata entityTypeMetadata : metadataByType.values()) {
            Set<Discriminator> discriminators = getAggregatedDiscriminators(entityTypeMetadata);
            LOGGER.debug("Aggregated discriminators of '{}' = '{}'", entityTypeMetadata, discriminators);
        }
        for (EntityTypeMetadata<EntityMetadata> entityTypeMetadata : metadataByType.values()) {
            Set<Discriminator> discriminators = aggregatedDiscriminators.get(entityTypeMetadata);
            for (Discriminator discriminator : discriminators) {
                Set<EntityTypeMetadata<EntityMetadata>> entityTypeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
                if (entityTypeMetadataOfDiscriminator == null) {
                    entityTypeMetadataOfDiscriminator = new HashSet<>();
                    typeMetadataByDiscriminator.put(discriminator, entityTypeMetadataOfDiscriminator);
                }
                entityTypeMetadataOfDiscriminator.add(entityTypeMetadata);
            }
        }
        LOGGER.debug("Type metadata by discriminators: '{}'", typeMetadataByDiscriminator);
    }

    /**
     * Determine the set of discriminators for one type, i.e. the discriminator of the type itself and of all it's super types.
     *
     * @param entityTypeMetadata The type.
     * @return The set of discriminators.
     */
    private Set<Discriminator> getAggregatedDiscriminators(EntityTypeMetadata<EntityMetadata> entityTypeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(entityTypeMetadata);
        if (discriminators == null) {
            discriminators = new HashSet<>();
            Discriminator discriminator = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
            if (discriminator != null) {
                discriminators.add(discriminator);
            }
            for (EntityTypeMetadata<EntityMetadata> superEntityTypeMetadata : entityTypeMetadata.getSuperTypes()) {
                discriminators.addAll(getAggregatedDiscriminators(superEntityTypeMetadata));
            }
            aggregatedDiscriminators.put(entityTypeMetadata, discriminators);
        }
        return discriminators;
    }

    /**
     * Return a {@link com.buschmais.cdo.spi.datastore.TypeMetadataSet} containing all types matching to the given entity discriminators.
     *
     * @param discriminators The discriminators.
     * @return The {@link com.buschmais.cdo.spi.datastore.TypeMetadataSet}.
     */
    public TypeMetadataSet getTypes(Set<Discriminator> discriminators) {
        // Get all types matching the discriminators
        Set<EntityTypeMetadata<EntityMetadata>> allEntityTypeMetadatas = new HashSet<>();
        for (Discriminator discriminator : discriminators) {
            Set<EntityTypeMetadata<EntityMetadata>> entityTypeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
            if (entityTypeMetadataOfDiscriminator != null) {
                for (EntityTypeMetadata<EntityMetadata> entityTypeMetadata : entityTypeMetadataOfDiscriminator) {
                    if (discriminators.containsAll(aggregatedDiscriminators.get(entityTypeMetadata))) {
                        allEntityTypeMetadatas.add(entityTypeMetadata);
                    }
                }
            }
        }
        // remove all super types if their sub types are already contained in the type set
        TypeMetadataSet<EntityMetadata> uniqueTypes = new TypeMetadataSet();
        for (EntityTypeMetadata<EntityMetadata> entityTypeMetadata : allEntityTypeMetadatas) {
            AnnotatedType annotatedType = entityTypeMetadata.getAnnotatedType();
            boolean subtype = false;
            for (Iterator<EntityTypeMetadata<EntityMetadata>> subTypeIterator = allEntityTypeMetadatas.iterator(); subTypeIterator.hasNext() && !subtype; ) {
                AnnotatedType otherAnnotatedType = subTypeIterator.next().getAnnotatedType();
                if (!annotatedType.equals(otherAnnotatedType) && annotatedType.getAnnotatedElement().isAssignableFrom(otherAnnotatedType.getAnnotatedElement())) {
                    subtype = true;
                }
            }
            if (!subtype) {
                uniqueTypes.add(entityTypeMetadata);
            }
        }
        return uniqueTypes;
    }

    public Set<Discriminator> getDiscriminators(EntityTypeMetadata<EntityMetadata> entityTypeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(entityTypeMetadata);
        return discriminators != null ? discriminators : Collections.<Discriminator>emptySet();
    }
}
