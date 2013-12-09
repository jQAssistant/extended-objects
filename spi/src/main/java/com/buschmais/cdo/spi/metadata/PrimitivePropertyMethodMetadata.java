package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

public class PrimitivePropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    public PrimitivePropertyMethodMetadata(PropertyMethod beanPropertyMethod, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod,datastoreMetadata);
    }
}
