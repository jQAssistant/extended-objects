package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;

public abstract class AbstractPropertyMethodMetadata extends AbstractMethodMetadata<BeanPropertyMethod> {

    protected AbstractPropertyMethodMetadata(BeanPropertyMethod beanMethod) {
        super(beanMethod);
    }

}
