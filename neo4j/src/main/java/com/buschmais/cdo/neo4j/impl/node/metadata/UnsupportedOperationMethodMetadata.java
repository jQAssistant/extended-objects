package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.UserMethod;

public class UnsupportedOperationMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<UserMethod, DatastoreMetadata> {

    protected UnsupportedOperationMethodMetadata(UserMethod beanMethod) {
        super(beanMethod, null);
    }
}
