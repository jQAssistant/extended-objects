package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;

public class PrimitivePropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    public PrimitivePropertyMethodMetadata(PropertyMethod propertyMethod, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
    }
}
