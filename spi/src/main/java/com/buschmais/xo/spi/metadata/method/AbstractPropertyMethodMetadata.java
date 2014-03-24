package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.reflection.PropertyMethod;

public abstract class AbstractPropertyMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<PropertyMethod, DatastoreMetadata> {

    protected AbstractPropertyMethodMetadata(PropertyMethod propertyMethod, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
    }

}
