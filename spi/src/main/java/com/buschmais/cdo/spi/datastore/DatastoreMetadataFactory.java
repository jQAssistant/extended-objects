package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedElement;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.AnnotatedType;
import com.buschmais.cdo.spi.reflection.PropertyMethod;

import java.util.Map;

/**
 * The metadata factory of the datastore.
 * <p>This factory is used on initialization to determine datastore specific entity, property, enumeration and relation metadata.</p>
 *
 * @param <EntityMetadata>        The type of entities used by the datastore.
 * @param <EntityDiscriminator>   The type of entity discriminators used by the datastore.
 * @param <RelationMetadata>      The type of relations used by the datastore.
 * @param <RelationDiscriminator> The type of relations discriminators used by the datastore.
 */
public interface DatastoreMetadataFactory<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    /**
     * Create the datastore specific entity metadata for the given type.
     *
     * @param annotatedType  The type.
     * @param metadataByType A map containing all registered type and their generic metadata.
     * @return An instance of datastore specific entity metadata.
     */
    EntityMetadata createEntityMetadata(AnnotatedType annotatedType, Map<Class<?>, TypeMetadata> metadataByType);

    /**
     * Create the datastore specific metadata for a method annotated with {@link com.buschmais.cdo.api.annotation.ImplementedBy}.
     *
     * @param annotatedMethod The method.
     * @return An instance of datastore specific method metadata.
     */
    <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(AnnotatedMethod annotatedMethod);

    /**
     * Create the datastore specific metadata for a property representing a collection of entities.
     *
     * @param propertyMethod The method.
     * @return An instance of datastore specific method metadata.
     */
    <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod propertyMethod);

    /**
     * Create the datastore specific metadata for a property representing a reference to an entity.
     *
     * @param propertyMethod The method.
     * @return An instance of datastore specific method metadata.
     */
    <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod propertyMethod);

    /**
     * Create the datastore specific metadata for a property representing primitive value.
     *
     * @param propertyMethod The method.
     * @return An instance of datastore specific method metadata.
     */
    <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPrimitivePropertyMetadata(PropertyMethod propertyMethod);

    /**
     * Create the datastore specific metadata for a property representing an indexed property.
     *
     * @param propertyMethod The method.
     * @return An instance of datastore specific method metadata.
     */
    <IndexedPropertyMetadata> IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod propertyMethod);

    /**
     * Create the datastore specific metadata for a relation.
     *
     * @param annotatedElement The annotated element, i.e. a {@link AnnotatedType} or {@link AnnotatedMethod}.
     * @param metadataByType   A map containing all registered type and their generic metadata.
     * @return An instance of datastore specific method metadata.
     */
    RelationMetadata createRelationMetadata(AnnotatedElement<?> annotatedElement, Map<Class<?>, TypeMetadata> metadataByType);

    RelationTypeMetadata.Direction getRelationDirection(PropertyMethod propertyMethod);
}
