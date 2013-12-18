package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Allows resolving types from entity discriminators as provided by the datastores.
 *
 * @param <Discriminator> The discriminator type of the datastore (e.g. Neo4j labels or strings for JSON stores).
 */
public class TypeResolver<EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeResolver.class);

    private Map<Class<?>, TypeMetadata<EntityMetadata>> metadataByType;
    private Map<TypeMetadata<EntityMetadata>, Set<Discriminator>> aggregatedDiscriminators = new HashMap<>();
    private Map<Discriminator, Set<TypeMetadata<EntityMetadata>>> typeMetadataByDiscriminator = new HashMap<>();

    /**
     * Constructor.
     *
     * @param metadataByType A map of all types with their metadata.
     */
    public TypeResolver(Map<Class<?>, TypeMetadata<EntityMetadata>> metadataByType) {
        LOGGER.info("Type metadata = '{}'", metadataByType);
        this.metadataByType = metadataByType;
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            Set<Discriminator> discriminators = getAggregatedDiscriminators(typeMetadata);
            LOGGER.debug("Aggregated discriminators of '{}' = '{}'", typeMetadata, discriminators);
        }
        for (TypeMetadata<EntityMetadata> typeMetadata : metadataByType.values()) {
            Set<Discriminator> discriminators = aggregatedDiscriminators.get(typeMetadata);
            for (Discriminator discriminator : discriminators) {
                Set<TypeMetadata<EntityMetadata>> typeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
                if (typeMetadataOfDiscriminator == null) {
                    typeMetadataOfDiscriminator = new HashSet<>();
                    typeMetadataByDiscriminator.put(discriminator, typeMetadataOfDiscriminator);
                }
                typeMetadataOfDiscriminator.add(typeMetadata);
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
    private Set<Discriminator> getAggregatedDiscriminators(TypeMetadata<EntityMetadata> typeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(typeMetadata);
        if (discriminators == null) {
            discriminators = new HashSet<>();
            Discriminator discriminator = typeMetadata.getDatastoreMetadata().getDiscriminator();
            if (discriminator != null) {
                discriminators.add(discriminator);
            }
            for (Class<?> superType : typeMetadata.getType().getInterfaces()) {
                TypeMetadata<EntityMetadata> superTypeMetadata = metadataByType.get(superType);
                discriminators.addAll(getAggregatedDiscriminators(superTypeMetadata));
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
    public TypeMetadataSet getTypes(Set<Discriminator> discriminators) {
        // Get all types matching the discriminators
        Set<TypeMetadata<EntityMetadata>> allTypeMetadatas = new HashSet<>();
        for (Discriminator discriminator : discriminators) {
            Set<TypeMetadata<EntityMetadata>> typeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
            if (typeMetadataOfDiscriminator != null) {
                for (TypeMetadata<EntityMetadata> typeMetadata : typeMetadataOfDiscriminator) {
                    if (discriminators.containsAll(aggregatedDiscriminators.get(typeMetadata))) {
                        allTypeMetadatas.add(typeMetadata);
                    }
                }
            }
        }
        // remove super allTypeMetadatas if sub types are already contained in the type set
        TypeMetadataSet<EntityMetadata> uniqueTypeMetadatas = new TypeMetadataSet();
        for (TypeMetadata<EntityMetadata> typeMetadata : allTypeMetadatas) {
            boolean subtype = false;
            for (Iterator<TypeMetadata<EntityMetadata>> subTypeMetadataIterator = allTypeMetadatas.iterator(); subTypeMetadataIterator.hasNext() && !subtype; ) {
                Class<?> otherType = subTypeMetadataIterator.next().getType();
                if (!typeMetadata.getType().equals(otherType) && typeMetadata.getType().isAssignableFrom(otherType)) {
                    subtype = true;
                }
            }
            if (!subtype) {
                uniqueTypeMetadatas.add(typeMetadata);
            }
        }
        return uniqueTypeMetadatas;
    }

    public Set<Discriminator> getDiscriminators(TypeMetadata<EntityMetadata> typeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(typeMetadata);
        return discriminators != null ? discriminators : Collections.<Discriminator>emptySet();
    }
}
