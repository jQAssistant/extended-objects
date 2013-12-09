package com.buschmais.cdo.neo4j.impl.node.metadata;


import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;

public class EnumPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private Class<? extends Enum<?>> enumerationType;

    protected EnumPropertyMethodMetadata(PropertyMethod beanPropertyMethod, Class<? extends Enum<?>> enumerationType, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod, datastoreMetadata);
        this.enumerationType = enumerationType;
    }

    public Class<? extends Enum<?>> getEnumerationType() {
        return enumerationType;
    }
}
