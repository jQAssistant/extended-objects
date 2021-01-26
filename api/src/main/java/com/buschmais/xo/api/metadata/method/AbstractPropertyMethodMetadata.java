package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;

public abstract class AbstractPropertyMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<PropertyMethod, DatastoreMetadata> {

    protected AbstractPropertyMethodMetadata(PropertyMethod propertyMethod, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
    }

}
