package com.buschmais.cdo.neo4j.impl.metadata;

public class PrimitivePropertyMethodMetadata extends AbstractPropertyMethodMetadata {

    private String propertyName;

    protected PrimitivePropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, String propertyName) {
        super(beanPropertyMethod);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
