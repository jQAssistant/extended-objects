package com.buschmais.cdo.store.json.impl;

import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.cdo.spi.metadata.RelationTypeMetadata;
import com.buschmais.cdo.spi.metadata.EntityTypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.spi.reflection.AnnotatedType;
import com.buschmais.cdo.store.json.impl.metadata.JsonNodeMetadata;

import java.util.Map;

public class JsonMetadataFactory implements DatastoreMetadataFactory<JsonNodeMetadata, String> {

    @Override
    public JsonNodeMetadata createEntityMetadata(AnnotatedType annotatedType, Map<Class<?>, EntityTypeMetadata<JsonNodeMetadata>> metadataByType) {
        return new JsonNodeMetadata(annotatedType.getAnnotatedElement().getName());
    }

    @Override
    public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(AnnotatedMethod annotatedMethod) {
        return null;
    }

    @Override
    public <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPrimitivePropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public <EnumPropertyMetadata> EnumPropertyMetadata createEnumPropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public <IndexedPropertyMetadata> IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public <RelationMetadata> RelationMetadata createRelationMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public RelationTypeMetadata.Direction getRelationDirection(PropertyMethod propertyMethod) {
        return null;
    }
}
