package com.buschmais.cdo.neo4j.impl.metadata;


public class EnumMethodMetadata extends AbstractMethodMetadata {

    private Class<? extends Enum<?>> enumerationType;

    protected EnumMethodMetadata(BeanPropertyMethod beanPropertyMethod, Class<? extends Enum<?>> enumerationType) {
        super(beanPropertyMethod);
        this.enumerationType = enumerationType;
    }

    public Class<? extends Enum<?>> getEnumerationType() {
        return enumerationType;
    }
}
