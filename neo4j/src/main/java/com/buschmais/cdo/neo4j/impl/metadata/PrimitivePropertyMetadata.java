package com.buschmais.cdo.neo4j.impl.metadata;

public class PrimitivePropertyMetadata extends AbstractPropertyMetadata {

    private String propertyName;

    protected PrimitivePropertyMetadata(NodeMetadataProvider.BeanProperty beanProperty, String propertyName) {
        super(beanProperty);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
