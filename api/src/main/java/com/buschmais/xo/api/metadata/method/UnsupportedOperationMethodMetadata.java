package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.UserMethod;

public class UnsupportedOperationMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<UserMethod, DatastoreMetadata> {

    public UnsupportedOperationMethodMetadata(UserMethod userMethod) {
        super(userMethod, null);
    }
}
