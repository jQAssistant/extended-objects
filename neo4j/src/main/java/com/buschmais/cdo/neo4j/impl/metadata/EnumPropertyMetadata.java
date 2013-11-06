package com.buschmais.cdo.neo4j.impl.metadata;


public class EnumPropertyMetadata extends AbstractPropertyMetadata {

    private Class<? extends Enum<?>> enumerationType;

    protected EnumPropertyMetadata(BeanProperty beanProperty, Class<? extends Enum<?>> enumerationType) {
        super(beanProperty);
        this.enumerationType = enumerationType;
    }

    public Class<? extends Enum<?>> getEnumerationType() {
        return enumerationType;
    }
}
