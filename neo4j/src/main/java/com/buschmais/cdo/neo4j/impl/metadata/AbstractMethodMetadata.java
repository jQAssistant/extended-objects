package com.buschmais.cdo.neo4j.impl.metadata;

public abstract class AbstractMethodMetadata {

    private BeanPropertyMethod beanMethod;

    protected AbstractMethodMetadata(BeanPropertyMethod beanMethod) {
        this.beanMethod = beanMethod;
    }

    public BeanPropertyMethod getBeanMethod() {
        return beanMethod;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "beanMethod=" + beanMethod + '}';
    }
}
