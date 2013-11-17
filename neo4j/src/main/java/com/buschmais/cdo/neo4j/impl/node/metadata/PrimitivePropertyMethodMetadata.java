package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;

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
