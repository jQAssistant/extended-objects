package com.buschmais.xo.impl.metadata;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final Map<EntityTypeMetadata<EntityMetadata>, Set<EntityTypeMetadata<EntityMetadata>>> aggregatedSuperTypes = new HashMap<>();
    private final Map<EntityTypeMetadata<EntityMetadata>, Set<EntityTypeMetadata<EntityMetadata>>> aggregatedSubTypes = new HashMap<>();

    private final Cache<Set<Discriminator>, TypeMetadataSet<EntityTypeMetadata<EntityMetadata>>> cache = Caffeine.newBuilder().maximumSize(64).build();

    /**
     * Constructor.
     *
     * @param metadataByType
     *            A map of all types with their metadata.
     */
    public EntityTypeMetadataResolver(Map<Class<?>, TypeMetadata> metadataByType, XOUnit.MappingConfiguration mappingConfiguration) {
        LOGGER.debug("Type metadata = '{}'", metadataByType);
        // Aggregate all super types
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            if (typeMetadata instanceof EntityTypeMetadata) {
                EntityTypeMetadata<EntityMetadata> entityTypeMetadata = (EntityTypeMetadata<EntityMetadata>) typeMetadata;
                aggregateSuperTypes(entityTypeMetadata);
            }
        }

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
                EntityTypeMetadata<EntityMetadata> metadata = (EntityTypeMetadata<EntityMetadata>) typeMetadata;
                    Set<Discriminator> discriminators = getAggregatedDiscriminators(metadata);
                    for (Discriminator discriminator : discriminators) {
                        Set<EntityTypeMetadata<EntityMetadata>> entityTypeMetadata = typeMetadataByDiscriminator.get(discriminator);
                        if (entityTypeMetadata == null) {
                            entityTypeMetadata = new HashSet<>();
                            typeMetadataByDiscriminator.put(discriminator, entityTypeMetadata);
                        }
                        entityTypeMetadata.add(metadata);
                    }
            }
        }
        LOGGER.debug("Type metadata by discriminators: '{}'", typeMetadataByDiscriminator);
        List<String> messages = new ArrayList<>();
        for (Map.Entry<Set<Discriminator>, Set<EntityTypeMetadata<EntityMetadata>>> entry : entityMetadataByDiscriminators.entrySet()) {
            if (entry.getValue().size() > 1) {
                String message = String.format("%s use the same set of discriminators %s.", entry.getValue(), entry.getKey());
                messages.add(message);

            }
        }
        if (!messages.isEmpty() && mappingConfiguration.isStrictValidation()) {
            throw new XOException("Mapping problems detected: " + messages);
        } else {
            for (String message : messages) {
                LOGGER.warn(message);
            }
        }
    }

    private Set<EntityTypeMetadata<EntityMetadata>> aggregateSuperTypes(EntityTypeMetadata<EntityMetadata> entityTypeMetadata) {
        Set<EntityTypeMetadata<EntityMetadata>> superTypes = aggregatedSuperTypes.get(entityTypeMetadata);
        if (superTypes == null) {
            superTypes = new TypeMetadataSet<>();
            for (TypeMetadata metadata : entityTypeMetadata.getSuperTypes()) {
                if (metadata instanceof EntityTypeMetadata) {
                    EntityTypeMetadata<EntityMetadata> superTypeMetadata = (EntityTypeMetadata<EntityMetadata>) metadata;
                    superTypes.add(superTypeMetadata);
                    addSubType(superTypeMetadata, entityTypeMetadata);
                    Set<EntityTypeMetadata<EntityMetadata>> aggregatedSuperTypes = aggregateSuperTypes(superTypeMetadata);
                    superTypes.addAll(aggregatedSuperTypes);
                    for (EntityTypeMetadata<EntityMetadata> superType : superTypes) {
                        addSubType(superType, entityTypeMetadata);
                    }
                }
            }
            aggregatedSuperTypes.put(entityTypeMetadata, superTypes);
        }
        return superTypes;
    };

    private void addSubType(EntityTypeMetadata<EntityMetadata> superType, EntityTypeMetadata<EntityMetadata> subType) {
        Set<EntityTypeMetadata<EntityMetadata>> subTypes = aggregatedSubTypes.get(superType);
        if (subTypes == null) {
            subTypes = new TypeMetadataSet<>();
            aggregatedSubTypes.put(superType, subTypes);
        }
        subTypes.add(subType);
    }

    /**
     * Determine the set of discriminators for one type, i.e. the discriminator of
     * the type itself and of all it's super types.
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
            for (EntityTypeMetadata<EntityMetadata> superTypeMetadata : aggregatedSuperTypes.get(typeMetadata)) {
                discriminator = superTypeMetadata.getDatastoreMetadata().getDiscriminator();
                if (discriminator != null) {
                    discriminators.add(discriminator);
                }
            }
            aggregatedDiscriminators.put(typeMetadata, discriminators);
        }
        return discriminators;
    }

    /**
     * Return a {@link com.buschmais.xo.spi.datastore.TypeMetadataSet} containing
     * all types matching to the given entity discriminators.
     *
     * @param discriminators
     *            The discriminators.
     * @return The {@link com.buschmais.xo.spi.datastore.TypeMetadataSet}.
     */
    public TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getTypes(Set<Discriminator> discriminators) {
        return cache.get(discriminators, key -> {
            LOGGER.info("Cache miss for discriminators {}.", key);
            TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> result = new TypeMetadataSet<>();
            for (Discriminator discriminator : key) {
                Set<EntityTypeMetadata<EntityMetadata>> candidates = typeMetadataByDiscriminator.get(discriminator);
                if (candidates != null) {
                    for (EntityTypeMetadata<EntityMetadata> candidate : candidates) {
                        Set<EntityTypeMetadata<EntityMetadata>> candidateSubTypes = aggregatedSubTypes.get(candidate);
                        if (candidateSubTypes == null || !result.containsAny(candidateSubTypes)) {
                            Set<Discriminator> entityDiscriminators = aggregatedDiscriminators.get(candidate);
                            if (key.size() >= entityDiscriminators.size() && key.containsAll(entityDiscriminators)) {
                                result.add(candidate);
                                result.removeAll(aggregatedSuperTypes.get(candidate));
                            }
                        }
                    }
                }
            }
            return result;
        });
    }

    public Set<Discriminator> getDiscriminators(EntityTypeMetadata<EntityMetadata> entityTypeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(entityTypeMetadata);
        return discriminators != null ? discriminators : Collections.<Discriminator> emptySet();
    }
}
