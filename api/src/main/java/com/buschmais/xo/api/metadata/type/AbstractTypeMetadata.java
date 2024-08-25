package com.buschmais.xo.api.metadata.type;

import java.util.Collection;

import com.buschmais.xo.api.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.method.MethodMetadata;
import com.buschmais.xo.api.metadata.reflection.AnnotatedType;

public abstract class AbstractTypeMetadata implements TypeMetadata {

    private final AnnotatedType annotatedType;

    private final Collection<MethodMetadata<?, ?>> properties;

    private final Collection<TypeMetadata> superTypes;

    private final IndexedPropertyMethodMetadata indexedProperty;

    protected AbstractTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties,
        IndexedPropertyMethodMetadata indexedProperty) {
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
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractTypeMetadata that = (AbstractTypeMetadata) o;
        return annotatedType.equals(that.annotatedType);
    }

    @Override
    public final int hashCode() {
        return annotatedType.hashCode();
    }

    @Override
    public final String toString() {
        String name = getClass().getSimpleName();
        if (annotatedType == null) {
            return name;
        }
        return name + "[" + annotatedType.getName() + "]";
    }
}
