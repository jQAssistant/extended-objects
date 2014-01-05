package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public abstract class AbstractTypeMetadata implements TypeMetadata {

    private AnnotatedType annotatedType;

    private Collection<MethodMetadata<?, ?>> properties;

    private Collection<TypeMetadata> superTypes;

    protected AbstractTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties) {
        this.annotatedType = annotatedType;
        this.superTypes = superTypes;
        this.properties = properties;
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

    @Override
    public String toString() {
        return "AbstractTypeMetadata{" +
                "type=" + annotatedType +
                '}';
    }
}
