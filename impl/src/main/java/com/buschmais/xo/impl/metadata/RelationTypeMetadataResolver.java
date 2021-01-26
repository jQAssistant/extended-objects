package com.buschmais.xo.impl.metadata;

import static com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.type.DatastoreEntityMetadata;
import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;
import com.buschmais.xo.api.metadata.type.CompositeTypeMetadata;
import com.buschmais.xo.api.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.method.MethodMetadata;
import com.buschmais.xo.api.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.api.metadata.type.TypeMetadata;
import com.buschmais.xo.api.metadata.reflection.AnnotatedType;

/**
 * Allows resolving types from relation discriminators as provided by the
 * datastores.
 *
 * @param <RelationDiscriminator>
 *            The discriminator type of the datastore (e.g. Neo4j relationship
 *            types or strings for JSON stores).
 */
public class RelationTypeMetadataResolver<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    private final Map<RelationDiscriminator, Set<RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator>>> relationMappings;
    private final Map<RelationPropertyKey, AbstractRelationPropertyMethodMetadata<RelationMetadata>> relationProperties;

    /**
     * Constructor.
     *
     * @param metadataByType
     *            A map of all types with their metadata.
     */
    public RelationTypeMetadataResolver(Map<Class<?>, TypeMetadata> metadataByType, EntityTypeMetadataResolver entityTypeMetadataResolver) {
        relationMappings = new HashMap<>();
        relationProperties = new HashMap<>();
        for (TypeMetadata typeMetadata : metadataByType.values()) {
            if (typeMetadata instanceof RelationTypeMetadata) {
                RelationTypeMetadata<RelationMetadata> relationTypeMetadata = (RelationTypeMetadata) typeMetadata;
                Class<?> outgoingType = relationTypeMetadata.getFromType();
                EntityTypeMetadata<EntityMetadata> outgoingTypeMetadata = (EntityTypeMetadata<EntityMetadata>) metadataByType.get(outgoingType);
                Set outgoingDiscriminators = entityTypeMetadataResolver.getDiscriminators(outgoingTypeMetadata);
                Class<?> incomingType = relationTypeMetadata.getToType();
                EntityTypeMetadata<EntityMetadata> incomingTypeMetadata = (EntityTypeMetadata<EntityMetadata>) metadataByType.get(incomingType);
                Set incomingDiscriminators = entityTypeMetadataResolver.getDiscriminators(incomingTypeMetadata);
                RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator> relationMapping = new RelationMapping<>(outgoingDiscriminators,
                        relationTypeMetadata, incomingDiscriminators);
                Set<RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator>> mappingSet = relationMappings
                        .get(relationTypeMetadata.getDatastoreMetadata().getDiscriminator());
                if (mappingSet == null) {
                    mappingSet = new HashSet<>();
                    relationMappings.put(relationTypeMetadata.getDatastoreMetadata().getDiscriminator(), mappingSet);
                }
                mappingSet.add(relationMapping);
            } else if (typeMetadata instanceof EntityTypeMetadata) {
                EntityTypeMetadata<EntityMetadata> entityTypeMetadata = (EntityTypeMetadata<EntityMetadata>) typeMetadata;
                for (MethodMetadata<?, ?> methodMetadata : entityTypeMetadata.getProperties()) {
                    if (methodMetadata instanceof AbstractRelationPropertyMethodMetadata<?>) {
                        AbstractRelationPropertyMethodMetadata<RelationMetadata> propertyMethodMetadata = (AbstractRelationPropertyMethodMetadata<RelationMetadata>) methodMetadata;
                        AnnotatedType relationType = propertyMethodMetadata.getRelationshipMetadata().getAnnotatedType();
                        if (relationType != null) {
                            Class<?> entityType = entityTypeMetadata.getAnnotatedType().getAnnotatedElement();
                            RelationTypeMetadata<?> relationTypeMetadata = (RelationTypeMetadata<?>) metadataByType.get(relationType.getAnnotatedElement());
                            Direction direction = propertyMethodMetadata.getDirection();
                            relationProperties.put(new RelationPropertyKey(entityType, relationTypeMetadata, direction), propertyMethodMetadata);
                        }
                    }
                }
            }
        }
    }

    /**
     * Determine the relation type for the given source discriminators, relation
     * descriminator and target discriminators.
     *
     * @param sourceDiscriminators
     *            The source discriminators.
     * @param discriminator
     *            The relation discriminator.
     * @param targetDiscriminators
     *            The target discriminators.
     * @return A set of matching relation types.
     */
    public CompositeTypeMetadata<RelationTypeMetadata<RelationMetadata>> getRelationTypes(Set<EntityDiscriminator> sourceDiscriminators,
                                                                                          RelationDiscriminator discriminator, Set<EntityDiscriminator> targetDiscriminators) {
        CompositeTypeMetadata<RelationTypeMetadata<RelationMetadata>> compositeTypeMetadata = new CompositeTypeMetadata<>();
        Set<RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator>> relations = relationMappings.get(discriminator);
        if (relations != null) {
            Set<RelationTypeMetadata<RelationMetadata>> relationTypes = new HashSet<>();
            for (RelationMapping<EntityDiscriminator, RelationMetadata, RelationDiscriminator> relation : relations) {
                Set<EntityDiscriminator> source = relation.getSource();
                Set<EntityDiscriminator> target = relation.getTarget();
                if (sourceDiscriminators.containsAll(source) && targetDiscriminators.containsAll(target)) {
                    RelationTypeMetadata<RelationMetadata> relationType = relation.getRelationType();
                    relationTypes.add(relationType);
                }
            }
            compositeTypeMetadata = new CompositeTypeMetadata<>(relationTypes);
        }
        return compositeTypeMetadata;
    }

    public AbstractRelationPropertyMethodMetadata<?> getRelationPropertyMethodMetadata(RelationTypeMetadata<?> relationTypeMetadata, Direction direction) {
        Class<?> containingType = null;
        switch (direction) {
        case FROM:
            containingType = relationTypeMetadata.getFromType();
            break;
        case TO:
            containingType = relationTypeMetadata.getToType();
            break;
        default:
            throw direction.createNotSupportedException();
        }
        RelationPropertyKey relationPropertyKey = new RelationPropertyKey(containingType, relationTypeMetadata, direction);
        AbstractRelationPropertyMethodMetadata<?> propertyMethodMetadata = relationProperties.get(relationPropertyKey);
        if (propertyMethodMetadata == null) {
            throw new XOException("Cannot resolve property in type '" + containingType.getName() + "' for relation type '"
                    + relationTypeMetadata.getAnnotatedType().getAnnotatedElement().getName() + "'.");
        }
        return propertyMethodMetadata;
    }

    private static class RelationPropertyKey {
        private final Class<?> entityType;
        private final RelationTypeMetadata<?> relationTypeMetadata;
        private final Direction direction;

        private RelationPropertyKey(Class<?> entityType, RelationTypeMetadata<?> relationTypeMetadata, Direction direction) {
            this.entityType = entityType;
            this.relationTypeMetadata = relationTypeMetadata;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RelationPropertyKey that = (RelationPropertyKey) o;
            if (direction != that.direction) {
                return false;
            }
            if (!entityType.equals(that.entityType)) {
                return false;
            }
            return relationTypeMetadata.equals(that.relationTypeMetadata);
        }

        @Override
        public int hashCode() {
            int result = entityType.hashCode();
            result = 31 * result + relationTypeMetadata.hashCode();
            result = 31 * result + direction.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "RelationPropertyKey{" + "entityType=" + entityType + ", relationTypeMetadata=" + relationTypeMetadata + ", direction=" + direction + '}';
        }
    }

    private static class RelationMapping<EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {
        private final Set<EntityDiscriminator> source;
        private final RelationTypeMetadata<RelationMetadata> relationType;
        private final Set<EntityDiscriminator> target;

        private RelationMapping(Set<EntityDiscriminator> source, RelationTypeMetadata<RelationMetadata> relationType, Set<EntityDiscriminator> target) {
            this.source = source;
            this.relationType = relationType;
            this.target = target;
        }

        private Set<EntityDiscriminator> getSource() {
            return source;
        }

        private RelationTypeMetadata<RelationMetadata> getRelationType() {
            return relationType;
        }

        private Set<EntityDiscriminator> getTarget() {
            return target;
        }
    }
}
