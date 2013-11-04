package com.buschmais.cdo.neo4j.impl.metadata;

public abstract class AbstractPropertyMetadata {

    private BeanProperty beanProperty;

    protected AbstractPropertyMetadata(BeanProperty beanProperty) {
        this.beanProperty = beanProperty;
    }

    public BeanProperty getBeanProperty() {
        return beanProperty;
    }

    @Override
    public String toString() {
        return "AbstractPropertyMetadata{" + "beanProperty=" + beanProperty + '}';
    }
}
