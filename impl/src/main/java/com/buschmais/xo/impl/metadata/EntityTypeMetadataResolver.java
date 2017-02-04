package com.buschmais.xo.impl.metadata;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

/**
 * Allows resolving types from entity discriminators as provided by the
 * datastores.
 *
 * @param <Discriminator>
 *            The discriminator type of the datastore (e.g. Neo4j labels or
 *            strings for JSON stores).
 */
public class EntityTypeMetadataResolver<EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeMetadataResolver.class);

    private final Map<EntityTypeMetadata<EntityMetadata>, Set<Discriminator>> aggregatedDiscriminators = new HashMap<>();
    private final Map<Discriminator, Set<EntityTypeMetadata<EntityMetadata>>> typeMetadataByDiscriminator = new HashMap<>();

    /**
     * Constructor.
     *
     * @param metadataByType
     *            A map of all types with their metadata.
     */
    public EntityTypeMetadataResolver(Map<Class<?>, TypeMetadata> metadataByType) {
        LOGGER.debug("Type metadata = '{}'", metadataByType);
        Map<Set<Discriminator>, Set<EntityTypeMetadata<EntityMetadata>>> entityMetadataByDiscriminators = new HashMap<>();
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            if (typeMetadata instanceof EntityTypeMetadata) {
                EntityTypeMetadata<EntityMetadata> entityTypeMetadata = (EntityTypeMetadata<EntityMetadata>) typeMetadata;
                Set<Discriminator> discriminators = getAggregatedDiscriminators(entityTypeMetadata);
                Set<EntityTypeMetadata<EntityMetadata>> typeMetadataOfDiscriminators = entityMetadataByDiscriminators.get(discriminators);
                if (typeMetadataOfDiscriminators == null) {
                    typeMetadataOfDiscriminators = new HashSet<>();
                    entityMetadataByDiscriminators.put(discriminators, typeMetadataOfDiscriminators);
                }
                typeMetadataOfDiscriminators.add(entityTypeMetadata);
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
        for (Map.Entry<Set<Discriminator>, Set<EntityTypeMetadata<EntityMetadata>>> entry : entityMetadataByDiscriminators.entrySet()) {
            if (entry.getValue().size() > 1) {
                LOGGER.info("{} use the same set of discriminators {}.", entry.getValue(), entry.getKey());
            }
        }
    }

    /**
     * Determine the set of discriminators for one type, i.e. the discriminator
     * of the type itself and of all it's super types.
     *
     * @param typeMetadata
     *            The type.
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
     * Return a {@link com.buschmais.xo.spi.datastore.TypeMetadataSet}
     * containing all types matching to the given entity discriminators.
     *
     * @param discriminators
     *            The discriminators.
     * @return The {@link com.buschmais.xo.spi.datastore.TypeMetadataSet}.
     */
    public TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getTypes(Set<Discriminator> discriminators) {
        // Get all types matching the discriminators
        TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> allEntityTypeMetadatas = new TypeMetadataSet<>();
        for (Discriminator discriminator : discriminators) {
            Set<EntityTypeMetadata<EntityMetadata>> entityTypeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
            if (entityTypeMetadataOfDiscriminator != null) {
                for (EntityTypeMetadata<EntityMetadata> entityTypeMetadata : entityTypeMetadataOfDiscriminator) {
                    Set<Discriminator> entityDiscriminators = aggregatedDiscriminators.get(entityTypeMetadata);
                    if (discriminators.size() >= entityDiscriminators.size() && discriminators.containsAll(entityDiscriminators)) {
                        allEntityTypeMetadatas.add(entityTypeMetadata);
                    }
                }
            }
        }
        return allEntityTypeMetadatas;
    }

    public Set<Discriminator> getDiscriminators(EntityTypeMetadata<EntityMetadata> entityTypeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(entityTypeMetadata);
        return discriminators != null ? discriminators : Collections.<Discriminator> emptySet();
    }
}
