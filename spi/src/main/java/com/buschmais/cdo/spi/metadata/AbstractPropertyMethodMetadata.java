package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

public abstract class AbstractPropertyMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<PropertyMethod, DatastoreMetadata> {

    protected AbstractPropertyMethodMetadata(PropertyMethod beanMethod, DatastoreMetadata datastoreMetadata) {
        super(beanMethod, datastoreMetadata);
    }

}
