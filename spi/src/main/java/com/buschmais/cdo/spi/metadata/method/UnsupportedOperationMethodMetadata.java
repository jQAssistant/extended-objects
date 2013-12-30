package com.buschmais.cdo.spi.metadata.method;

import com.buschmais.cdo.spi.reflection.UserMethod;

public class UnsupportedOperationMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<UserMethod, DatastoreMetadata> {

    public UnsupportedOperationMethodMetadata(UserMethod userMethod) {
        super(userMethod, null);
    }
}
