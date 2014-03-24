package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.reflection.PropertyMethod;

public class PrimitivePropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    public PrimitivePropertyMethodMetadata(PropertyMethod propertyMethod, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
    }
}
