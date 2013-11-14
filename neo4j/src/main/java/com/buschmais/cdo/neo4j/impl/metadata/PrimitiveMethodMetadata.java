package com.buschmais.cdo.neo4j.impl.metadata;

public class PrimitiveMethodMetadata extends AbstractMethodMetadata {

    private String propertyName;

    protected PrimitiveMethodMetadata(BeanPropertyMethod beanPropertyMethod, String propertyName) {
        super(beanPropertyMethod);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
