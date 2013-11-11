package com.buschmais.cdo.neo4j.impl.metadata;

public class PrimitivePropertyMetadata extends AbstractPropertyMetadata {

    private String propertyName;

    private boolean indexed;

    protected PrimitivePropertyMetadata(BeanProperty beanProperty, String propertyName, boolean indexed) {
        super(beanProperty);
        this.propertyName = propertyName;
        this.indexed = indexed;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isIndexed() {
        return indexed;
    }
}
