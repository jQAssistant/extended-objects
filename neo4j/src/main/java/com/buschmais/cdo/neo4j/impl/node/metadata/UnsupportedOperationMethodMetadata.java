package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;

public class UnsupportedOperationMethodMetadata<DatastoreMetadata> extends AbstractMethodMetadata<BeanMethod, DatastoreMetadata> {

    protected UnsupportedOperationMethodMetadata(BeanMethod beanMethod) {
        super(beanMethod);
    }
}
