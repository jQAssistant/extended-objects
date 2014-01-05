package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.type.DatastoreTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
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

    private Map<EntityTypeMetadata<EntityMetadata>, Set<Discriminator>> aggregatedDiscriminators = new HashMap<>();
    private Map<Discriminator, Set<EntityTypeMetadata<EntityMetadata>>> typeMetadataByDiscriminator = new HashMap<>();

    /**
     * Constructor.
     *
     * @param metadataByType A map of all types with their metadata.
     */
    public EntityTypeMetadataResolver(Map<Class<?>, TypeMetadata> metadataByType) {
        LOGGER.debug("Type metadata = '{}'", metadataByType);
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            if (typeMetadata instanceof EntityTypeMetadata) {
                Set<Discriminator> discriminators = getAggregatedDiscriminators((EntityTypeMetadata<EntityMetadata>) typeMetadata);
                LOGGER.debug("Aggregated discriminators of '{}' = '{}'", typeMetadata, discriminators);
            }
        }
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            if (typeMetadata instanceof EntityTypeMetadata) {
                Set<Discriminator> discriminators = aggregatedDiscriminators.get(typeMetadata);
                for (Discriminator discriminator : discriminators) {
                    Set<EntityTypeMetadata<EntityMetadata>> entityTypeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
                    if (entityTypeMetadataOfDiscriminator == null) {
                        entityTypeMetadataOfDiscriminator = new HashSet<>();
                        typeMetadataByDiscriminator.put(discriminator, entityTypeMetadataOfDiscriminator);
                    }
                    entityTypeMetadataOfDiscriminator.add((EntityTypeMetadata<EntityMetadata>) typeMetadata);
                }
            }
        }
        LOGGER.debug("Type metadata by discriminators: '{}'", typeMetadataByDiscriminator);
    }

    /**
     * Determine the set of discriminators for one type, i.e. the discriminator of the type itself and of all it's super types.
     *
     * @param typeMetadata The type.
     * @return The set of discriminators.
     */
    private Set<Discriminator> getAggregatedDiscriminators(EntityTypeMetadata<EntityMetadata> typeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(typeMetadata);
        if (discriminators == null) {
            discriminators = new HashSet<>();
            Discriminator discriminator = typeMetadata.getDatastoreMetadata().getDiscriminator();
            if (discriminator != null) {
                discriminators.add(discriminator);
            }
            for (TypeMetadata superTypeMetadata : typeMetadata.getSuperTypes()) {
                if (superTypeMetadata instanceof EntityTypeMetadata) {
                    discriminators.addAll(getAggregatedDiscriminators((EntityTypeMetadata<EntityMetadata>) superTypeMetadata));
                }
            }
            aggregatedDiscriminators.put(typeMetadata, discriminators);
        }
        return discriminators;
    }

    /**
     * Return a {@link com.buschmais.cdo.spi.datastore.TypeMetadataSet} containing all types matching to the given entity discriminators.
     *
     * @param discriminators The discriminators.
     * @return The {@link com.buschmais.cdo.spi.datastore.TypeMetadataSet}.
     */
    public TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getTypes(Set<Discriminator> discriminators) {
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
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> uniqueTypes = new TypeMetadataSet();
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
