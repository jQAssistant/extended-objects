package com.buschmais.cdo.neo4j.impl.metadata;

public abstract class AbstractPropertyMetadata {

    private NodeMetadataProvider.BeanProperty beanProperty;

    protected AbstractPropertyMetadata(NodeMetadataProvider.BeanProperty beanProperty) {
        this.beanProperty = beanProperty;
    }

    public NodeMetadataProvider.BeanProperty getBeanProperty() {
        return beanProperty;
    }

    @Override
    public String toString() {
        return "AbstractPropertyMetadata{" + "beanProperty=" + beanProperty + '}';
    }
}
