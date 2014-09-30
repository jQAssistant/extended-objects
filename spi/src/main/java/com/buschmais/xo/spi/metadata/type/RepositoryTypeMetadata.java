package com.buschmais.xo.spi.metadata.type;

import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedType;

import java.util.Collection;

public class RepositoryTypeMetadata extends AbstractTypeMetadata {

    public RepositoryTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties) {
        super(annotatedType, superTypes, properties, null);
    }
}
