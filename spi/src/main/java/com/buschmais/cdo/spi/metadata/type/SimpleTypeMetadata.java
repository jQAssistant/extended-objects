package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

/**
 * Represe
 */
public class SimpleTypeMetadata extends AbstractTypeMetadata {

    public SimpleTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties, IndexedPropertyMethodMetadata indexedProperty) {
        super(annotatedType, superTypes, properties, indexedProperty);
    }
}
