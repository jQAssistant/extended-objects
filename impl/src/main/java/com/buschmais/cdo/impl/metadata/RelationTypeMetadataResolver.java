package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

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

    private final Map<RelationDiscriminator, Set<RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator>>> relationMappings;
    private final Map<Class<?>, Map<RelationTypeMetadata<?>, AbstractRelationPropertyMethodMetadata<?>>> relationProperties;

    /**
     * Constructor.
     *
     * @param metadataByType A map of all types with their metadata.
     */
    public RelationTypeMetadataResolver(Map<Class<?>, TypeMetadata> metadataByType) {
        relationMappings = new HashMap<>();
        relationProperties = new HashMap<>();
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
            } else if (typeMetadata instanceof EntityTypeMetadata) {
                EntityTypeMetadata entityTypeMetadata = (EntityTypeMetadata) typeMetadata;
                for (MethodMetadata<?, ?> methodMetadata : entityTypeMetadata.getProperties()) {
                    if (methodMetadata instanceof AbstractRelationPropertyMethodMetadata<?>) {
                        AbstractRelationPropertyMethodMetadata propertyMethodMetadata = (AbstractRelationPropertyMethodMetadata) methodMetadata;
                        AnnotatedType relationType = propertyMethodMetadata.getRelationshipMetadata().getAnnotatedType();
                        if (relationType != null) {
                            RelationTypeMetadata<?> relationTypeMetadata = (RelationTypeMetadata<?>) metadataByType.get(relationType.getAnnotatedElement());
                            Class<?> entityType = entityTypeMetadata.getAnnotatedType().getAnnotatedElement();
                            Map<RelationTypeMetadata<?>, AbstractRelationPropertyMethodMetadata<?>> relationProperties = this.relationProperties.get(entityType);
                            if (relationProperties == null) {
                                relationProperties = new HashMap<>();
                                this.relationProperties.put(entityType, relationProperties);
                            }
                            relationProperties.put(relationTypeMetadata, propertyMethodMetadata);
                        }
                    }
                }
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

    public AbstractRelationPropertyMethodMetadata<?> getRelationPropertyMethodMetadata(Class<?> type, RelationTypeMetadata<?> relationTypeMetadata, RelationTypeMetadata.Direction direction) {
        Class<?> containingType = null;
        switch (direction) {
            case OUTGOING:
                if (relationTypeMetadata.getOutgoingType().isAssignableFrom(type)) {
                    containingType = relationTypeMetadata.getOutgoingType();
                }
                break;
            case INCOMING:
                if (relationTypeMetadata.getIncomingType().isAssignableFrom(type)) {
                    containingType = relationTypeMetadata.getIncomingType();
                }
                break;
            default:
                throw direction.createNotSupportedException();
        }
        if (containingType == null) {
            throw new CdoException("Cannot resolve containing entity type for relation type '" + relationTypeMetadata.getAnnotatedType().getName() + "'.");
        }
        Map<RelationTypeMetadata<?>, AbstractRelationPropertyMethodMetadata<?>> relationProperties = this.relationProperties.get(containingType);
        AbstractRelationPropertyMethodMetadata<?> propertyMethodMetadata = relationProperties.get(relationTypeMetadata);
        if (propertyMethodMetadata == null) {
            throw new CdoException("Cannot resolve property for relation type '" + relationTypeMetadata.getAnnotatedType().getName());
        }
        return propertyMethodMetadata;
    }

    private static class RelationMapping<EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {
        private final EntityDiscriminator source;
        private final RelationTypeMetadata<RelationMetadata> relationType;
        private final EntityDiscriminator target;

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
