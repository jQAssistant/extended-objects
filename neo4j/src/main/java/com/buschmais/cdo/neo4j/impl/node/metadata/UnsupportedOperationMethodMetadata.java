package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;

public class UnsupportedOperationMethodMetadata extends AbstractMethodMetadata<BeanMethod> {

    protected UnsupportedOperationMethodMetadata(BeanMethod beanMethod) {
        super(beanMethod);
    }
}
