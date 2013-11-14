package com.buschmais.cdo.neo4j.impl.metadata;

public abstract class AbstractPropertyMethodMetadata extends AbstractMethodMetadata<BeanPropertyMethod> {

    protected AbstractPropertyMethodMetadata(BeanPropertyMethod beanMethod) {
        super(beanMethod);
    }

}
