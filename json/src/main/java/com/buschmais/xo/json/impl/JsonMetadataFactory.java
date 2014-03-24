package com.buschmais.xo.json.impl;

import com.buschmais.xo.json.impl.metadata.JsonNodeMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedElement;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;
import com.buschmais.xo.spi.reflection.AnnotatedType;
import com.buschmais.xo.spi.reflection.PropertyMethod;

import java.util.Map;

public class JsonMetadataFactory implements DatastoreMetadataFactory<JsonNodeMetadata, String, JsonRelationMetadata, String> {

    @Override
    public JsonNodeMetadata createEntityMetadata(AnnotatedType annotatedType, Map<Class<?>, TypeMetadata> metadataByType) {
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
    public <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public <IndexedPropertyMetadata> IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public JsonRelationMetadata createRelationMetadata(AnnotatedElement<?> annotatedElement, Map<Class<?>, TypeMetadata> metadataByType) {
        return null;
    }
}
