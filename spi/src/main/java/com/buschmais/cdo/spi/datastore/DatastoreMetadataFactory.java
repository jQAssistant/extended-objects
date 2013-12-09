package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.RelationMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.reflection.BeanMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;

import java.util.Map;

public interface DatastoreMetadataFactory<EntityMetadata> {
    // Metadata

    EntityMetadata createEntityMetadata(Class<?> type, Map<Class<?>, TypeMetadata> metadataByType);

    <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(BeanMethod beanMethod);

    <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod beanPropertyMethod);

    <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod beanPropertyMethod);

    <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPrimitvePropertyMetadata(PropertyMethod beanPropertyMethod);

    <EnumPropertyMetadata> EnumPropertyMetadata createEnumPropertyMetadata(PropertyMethod beanPropertyMethod);

    <ImplementedByMetadata> ImplementedByMetadata createIndexedPropertyMetadata(PropertyMethod beanMethod);

    <RelationMetadata> RelationMetadata createRelationMetadata(PropertyMethod beanPropertyMethod);

    RelationMetadata.Direction getRelationDirection(PropertyMethod beanPropertyMethod);
}