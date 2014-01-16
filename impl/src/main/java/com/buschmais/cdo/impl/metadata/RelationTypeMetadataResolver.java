package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.metadata.method.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Allows resolving types from relation discriminators as provided by the datastores.
 *
 * @param <RelationDiscriminator> The discriminator type of the datastore (e.g. Neo4j relationship types or strings for JSON stores).
 */
public class RelationTypeMetadataResolver<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    private Map<RelationDiscriminator, Set<RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator>>> relationMappings;

    /**
     * Constructor.
     *
     * @param metadataByType A map of all types with their metadata.
     */
    public RelationTypeMetadataResolver(Map<Class<?>, TypeMetadata> metadataByType) {
        relationMappings = new HashMap<>();
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            if (typeMetadata instanceof RelationTypeMetadata) {
                RelationTypeMetadata<RelationMetadata> relationTypeMetadata = (RelationTypeMetadata) typeMetadata;
                Class<?> outgoingType = relationTypeMetadata.getOutgoingType();
                EntityTypeMetadata<EntityMetadata> outgoingTypeMetadata = (EntityTypeMetadata<EntityMetadata>) metadataByType.get(outgoingType);
                Class<?> incomingType = relationTypeMetadata.getIncomingType();
                EntityTypeMetadata<EntityMetadata> incomingTypeMetadata = (EntityTypeMetadata<EntityMetadata>) metadataByType.get(incomingType);
                RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator> relationMapping = new RelationMapping<>(outgoingTypeMetadata.getDatastoreMetadata().getDiscriminator(), relationTypeMetadata, incomingTypeMetadata.getDatastoreMetadata().getDiscriminator());
                Set<RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator>> mappingSet = relationMappings.get(relationTypeMetadata.getDatastoreMetadata().getDiscriminator());
                if (mappingSet == null) {
                    mappingSet = new HashSet<>();
                    relationMappings.put(relationTypeMetadata.getDatastoreMetadata().getDiscriminator(), mappingSet);
                }
                mappingSet.add(relationMapping);
            }
        }
    }

    public TypeMetadataSet<RelationTypeMetadata<RelationMetadata>> getRelationTypes(Set<EntityDiscriminator> sourceDiscriminators, RelationDiscriminator discriminator, Set<EntityDiscriminator> targetDiscriminators) {
        TypeMetadataSet<RelationTypeMetadata<RelationMetadata>> types = new TypeMetadataSet<>();
        Set<RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator>> relations = relationMappings.get(discriminator);
        if (relations == null) {
            throw new CdoException("Cannot resolve relation from discriminator '" + discriminator + "'");
        }
        for (RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator> relation : relations) {
            EntityDiscriminator source = relation.getSource();
            EntityDiscriminator target = relation.getTarget();
            if (sourceDiscriminators.contains(source) && targetDiscriminators.contains(target)) {
                types.add(relation.getRelationType());
            }
        }
        return types;
    }

    private static class RelationMapping<EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {
        private EntityDiscriminator source;
        private RelationTypeMetadata<RelationMetadata> relationType;
        private EntityDiscriminator target;

        private RelationMapping(EntityDiscriminator source, RelationTypeMetadata<RelationMetadata> relationType, EntityDiscriminator target) {
            this.source = source;
            this.relationType = relationType;
            this.target = target;
        }

        private EntityDiscriminator getSource() {
            return source;
        }

        private RelationTypeMetadata<RelationMetadata> getRelationType() {
            return relationType;
        }

        private EntityDiscriminator getTarget() {
            return target;
        }
    }
}
