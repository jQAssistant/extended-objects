package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.reflection.UserMethod;

public class UnsupportedOperationMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<UserMethod, DatastoreMetadata> {

    public UnsupportedOperationMethodMetadata(UserMethod userMethod) {
        super(userMethod, null);
    }
}
