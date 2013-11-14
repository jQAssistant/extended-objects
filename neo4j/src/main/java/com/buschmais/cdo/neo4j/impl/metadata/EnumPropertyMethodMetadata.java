package com.buschmais.cdo.neo4j.impl.metadata;


public class EnumPropertyMethodMetadata extends AbstractPropertyMethodMetadata {

    private Class<? extends Enum<?>> enumerationType;

    protected EnumPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, Class<? extends Enum<?>> enumerationType) {
        super(beanPropertyMethod);
        this.enumerationType = enumerationType;
    }

    public Class<? extends Enum<?>> getEnumerationType() {
        return enumerationType;
    }
}
