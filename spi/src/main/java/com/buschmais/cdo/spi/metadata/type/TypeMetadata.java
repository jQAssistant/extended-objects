package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public interface TypeMetadata {

    AnnotatedType getAnnotatedType();

    Collection<TypeMetadata> getSuperTypes();

    Collection<MethodMetadata<?, ?>> getProperties();

}
