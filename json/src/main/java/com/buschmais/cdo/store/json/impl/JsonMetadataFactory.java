package com.buschmais.cdo.store.json.impl;

import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.cdo.spi.metadata.RelationMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.reflection.BeanMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.store.json.impl.metadata.JsonNodeMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonMetadataFactory implements DatastoreMetadataFactory<JsonNodeMetadata, String> {

    @Override
    public JsonNodeMetadata createEntityMetadata(Class<?> type, Map<Class<?>, TypeMetadata<JsonNodeMetadata>> metadataByType) {
        List<String> aggregatedTypeNames = new ArrayList<>();
        aggregatedTypeNames.add(type.getName());
        for (Class<?> superType : type.getInterfaces()) {
            aggregatedTypeNames.add(superType.getName());
        }
        return new JsonNodeMetadata("type", aggregatedTypeNames);
    }

    @Override
    public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(BeanMethod beanMethod) {
        return null;
    }

    @Override
    public <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod beanPropertyMethod) {
        return null;
    }

    @Override
    public <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod beanPropertyMethod) {
        return null;
    }

    @Override
    public <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPrimitvePropertyMetadata(PropertyMethod beanPropertyMethod) {
        return null;
    }

    @Override
    public <EnumPropertyMetadata> EnumPropertyMetadata createEnumPropertyMetadata(PropertyMethod beanPropertyMethod) {
        return null;
    }

    @Override
    public <IndexedPropertyMetadata> IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod beanMethod) {
        return null;
    }

    @Override
    public <RelationMetadata> RelationMetadata createRelationMetadata(PropertyMethod beanPropertyMethod) {
        return null;
    }

    @Override
    public RelationMetadata.Direction getRelationDirection(PropertyMethod beanPropertyMethod) {
        return null;
    }
}
