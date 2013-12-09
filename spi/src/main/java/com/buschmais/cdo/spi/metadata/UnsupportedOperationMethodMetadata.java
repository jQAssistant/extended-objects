package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.UserMethod;

public class UnsupportedOperationMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<UserMethod, DatastoreMetadata> {

    public UnsupportedOperationMethodMetadata(UserMethod beanMethod) {
        super(beanMethod, null);
    }
}
