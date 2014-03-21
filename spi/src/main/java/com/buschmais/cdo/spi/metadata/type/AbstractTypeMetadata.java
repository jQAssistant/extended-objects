package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public abstract class AbstractTypeMetadata implements TypeMetadata {

    private final AnnotatedType annotatedType;

    private final Collection<MethodMetadata<?, ?>> properties;

    private final Collection<TypeMetadata> superTypes;

    private final IndexedPropertyMethodMetadata indexedProperty;

    protected AbstractTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties, IndexedPropertyMethodMetadata indexedProperty) {
        this.annotatedType = annotatedType;
        this.superTypes = superTypes;
        this.properties = properties;
        this.indexedProperty = indexedProperty;
    }

    @Override
    public AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public Collection<TypeMetadata> getSuperTypes() {
        return superTypes;
    }

    @Override
    public Collection<MethodMetadata<?, ?>> getProperties() {
        return properties;
    }


    public IndexedPropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }

    @Override
    public String toString() {
        return "AbstractTypeMetadata{" +
                "type=" + annotatedType +
                '}';
    }
}
