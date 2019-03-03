package com.buschmais.xo.spi.metadata.type;

import java.util.Collection;

import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedType;

/**
 * Represe
 */
public class SimpleTypeMetadata extends AbstractTypeMetadata {

    public SimpleTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties,
            IndexedPropertyMethodMetadata indexedProperty) {
        super(annotatedType, superTypes, properties, indexedProperty);
    }
}
