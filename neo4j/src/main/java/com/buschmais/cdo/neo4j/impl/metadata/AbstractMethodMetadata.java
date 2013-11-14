package com.buschmais.cdo.neo4j.impl.metadata;

public abstract class AbstractMethodMetadata<B extends BeanMethod> {

    private B beanMethod;

    protected AbstractMethodMetadata(B beanMethod) {
        this.beanMethod = beanMethod;
    }

    public B getBeanMethod() {
        return beanMethod;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "beanMethod=" + beanMethod + '}';
    }
}
