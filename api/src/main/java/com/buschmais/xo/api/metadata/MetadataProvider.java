package com.buschmais.xo.api.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.*;

/**
 * Defines the interface for the metadata provider.
 *
 * @param <EntityMetadata>
 *     The type of datastore specific entity metadata.
 * @param <EntityDiscriminator>
 *     The type of datastore specific entity type discriminators.
 * @param <RelationMetadata>
 *     The type of datastore specific relation metadata.
 * @param <RelationDiscriminator>
 *     The type of datastore specific relationtype discriminators.
 */
public interface MetadataProvider<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    /**
     * Determine the {@link CompositeTypeMetadata} for a given set of entity discriminators.
     *
     * @param entityDiscriminators
     *     The entity descriminators.
     * @return The {@link CompositeTypeMetadata}.
     */
    CompositeTypeMetadata<EntityTypeMetadata<EntityMetadata>> getTypes(Set<EntityDiscriminator> entityDiscriminators);

    /**
     * Determine the set of entity discriminators for the given {@link CompositeTypeMetadata}.
     *
     * @param types
     *     The {@link CompositeTypeMetadata}.
     * @return The set of discriminators.
     */
    Set<EntityDiscriminator> getEntityDiscriminators(CompositeTypeMetadata<EntityTypeMetadata<EntityMetadata>> types);

    /**
     * Determine the {@link CompositeTypeMetadata} for a given relation discriminator.
     *
     * @param discriminator
     *     The relation descriminator.
     * @return The {@link CompositeTypeMetadata}.
     */
    CompositeTypeMetadata<RelationTypeMetadata<RelationMetadata>> getRelationTypes(Set<EntityDiscriminator> sourceDiscriminators,
        RelationDiscriminator discriminator, Set<EntityDiscriminator> targetDiscriminators);

    /**
     * Return a collection of all registered entity type metadata.
     *
     * @return The collection of all registered entity type metadata.
     */
    Map<Class<?>, TypeMetadata> getRegisteredMetadata();

    /**
     * Return the entity metadata for a specific type.
     *
     * @param type
     *     The type.
     * @return The entity metadata.
     */
    EntityTypeMetadata<EntityMetadata> getEntityMetadata(Class<?> type);

    /**
     * Return the relation metadata for a specific type.
     *
     * @param relationType
     *     The relation type.
     * @return The relation metadata.
     */
    RelationTypeMetadata<RelationMetadata> getRelationMetadata(Class<?> relationType);

    RelationTypeMetadata.Direction getRelationDirection(Set<Class<?>> sourceTypes, RelationTypeMetadata<RelationMetadata> relationMetadata,
        Set<Class<?>> targetTypes);

    /**
     * Return the repository metadata for a specific type.
     *
     * @param repositoryType
     *     The repository type.
     * @return The repository metadata.
     */
    RepositoryTypeMetadata getRepositoryMetadata(Class<?> repositoryType);

    /**
     * Return the property which represents a relation in an entity.
     *
     * @param entityType
     *     The entity.
     * @param relationType
     *     The relation type.
     * @param direction
     *     The direction.
     * @param <R>
     *     The Relation type.
     * @return The property metadata.
     */
    <R> AbstractRelationPropertyMethodMetadata<?> getPropertyMetadata(Class<?> entityType, Class<R> relationType, RelationTypeMetadata.Direction direction);

    /**
     * Return the annotation which represents a query definition.
     *
     * @param annotatedElement
     *     The annotated element.
     * @param <QL>
     *     The annotation type.
     * @return The annotation or null if the element is not annotated with a query.
     */
    <QL extends Annotation> QL getQuery(AnnotatedElement annotatedElement);

    CompositeTypeMetadata<EntityTypeMetadata<EntityMetadata>> getEffectiveTypes(Class<?> type, Class<?>[] types);
}
