package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.TypeSet;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Allows resolving types from entity discriminators as provided by the datastores.
 *
 * @param <Discriminator> The discriminator type of the datastore (e.g. Neo4j labels or strings for JSON stores).
 */
public class TypeResolver<Discriminator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeResolver.class);

    private Map<Class<?>, TypeMetadata<DatastoreEntityMetadata<Discriminator>>> metadataByType;
    private Map<TypeMetadata<DatastoreEntityMetadata<Discriminator>>, Set<Discriminator>> aggregatedDiscriminators = new HashMap<>();
    private Map<Discriminator, Set<TypeMetadata>> typeMetadataByDiscriminator = new HashMap<>();

    /**
     * Constructor.
     *
     * @param metadataByType A map of all types with their metadata.
     */
    public TypeResolver(Map<Class<?>, TypeMetadata<DatastoreEntityMetadata<Discriminator>>> metadataByType) {
        LOGGER.info("Type metadata = '{}'", metadataByType);
        this.metadataByType = metadataByType;
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            Set<Discriminator> discriminators = getAggregatedDiscriminators(typeMetadata);
            LOGGER.debug("Aggregated discriminators of '{}' = '{}'", typeMetadata, discriminators);
        }
        for (TypeMetadata<DatastoreEntityMetadata<Discriminator>> typeMetadata : metadataByType.values()) {
            Set<Discriminator> discriminators = aggregatedDiscriminators.get(typeMetadata);
            for (Discriminator discriminator : discriminators) {
                Set<TypeMetadata> typeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
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
    private Set<Discriminator> getAggregatedDiscriminators(TypeMetadata<DatastoreEntityMetadata<Discriminator>> typeMetadata) {
        Set<Discriminator> discriminators = aggregatedDiscriminators.get(typeMetadata);
        if (discriminators == null) {
            discriminators = new HashSet<>();
            Discriminator discriminator = typeMetadata.getDatastoreMetadata().getDiscriminator();
            if (discriminator != null) {
                discriminators.add(discriminator);
            }
            for (Class<?> superType : typeMetadata.getType().getInterfaces()) {
                TypeMetadata<DatastoreEntityMetadata<Discriminator>> superTypeMetadata = metadataByType.get(superType);
                discriminators.addAll(getAggregatedDiscriminators(superTypeMetadata));
            }
            aggregatedDiscriminators.put(typeMetadata, discriminators);
        }
        return discriminators;
    }

    /**
     * Return a {@link TypeSet} containing all types matching to the given entity discriminators.
     *
     * @param discriminators The discriminators.
     * @return The {@link TypeSet}.
     */
    public TypeSet getTypes(Set<Discriminator> discriminators) {
        // Get all types matching the discriminators
        Set<Class<?>> allTypes = new HashSet<>();
        for (Discriminator discriminator : discriminators) {
            Set<TypeMetadata> typeMetadataOfDiscriminator = typeMetadataByDiscriminator.get(discriminator);
            if (typeMetadataOfDiscriminator != null) {
                for (TypeMetadata<DatastoreEntityMetadata<Discriminator>> typeMetadata : typeMetadataOfDiscriminator) {
                    if (discriminators.containsAll(aggregatedDiscriminators.get(typeMetadata))) {
                        allTypes.add(typeMetadata.getType());
                    }
                }
            }
        }
        // remove super allTypes if sub types are already contained in the type set
        TypeSet uniqueTypes = new TypeSet();
        for (Class<?> type : allTypes) {
            boolean subtype = false;
            for (Iterator<Class<?>> subTypeIterator = allTypes.iterator(); subTypeIterator.hasNext() && !subtype; ) {
                Class<?> otherType = subTypeIterator.next();
                if (!type.equals(otherType) && type.isAssignableFrom(otherType)) {
                    subtype = true;
                }
            }
            if (!subtype) {
                uniqueTypes.add(type);
            }
        }
        return uniqueTypes;
    }
}
