package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.UserDefinedMethod;

public class UnsupportedOperationMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<UserDefinedMethod, DatastoreMetadata> {

    protected UnsupportedOperationMethodMetadata(UserDefinedMethod beanMethod) {
        super(beanMethod, null);
    }
}
