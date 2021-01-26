package com.buschmais.xo.api.metadata.type;

import java.util.Collection;

import com.buschmais.xo.api.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.method.MethodMetadata;
import com.buschmais.xo.api.metadata.reflection.AnnotatedType;

/**
 * Represents metadata for primitive types.
 */
public class SimpleTypeMetadata extends AbstractTypeMetadata {

    public SimpleTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties,
            IndexedPropertyMethodMetadata indexedProperty) {
        super(annotatedType, superTypes, properties, indexedProperty);
    }
}
