package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;

public class IndexedPropertyMethodMetadata extends AbstractPropertyMethodMetadata {

    private PrimitivePropertyMethodMetadata propertyMethodMetadata;

    private boolean create;

    protected IndexedPropertyMethodMetadata(BeanPropertyMethod beanPropertyMethod, PrimitivePropertyMethodMetadata propertyMethodMetadata, boolean create) {
        super(beanPropertyMethod);
        this.propertyMethodMetadata = propertyMethodMetadata;
        this.create = create;
    }

    public PrimitivePropertyMethodMetadata getPropertyMethodMetadata() {
        return propertyMethodMetadata;
    }

    public boolean isCreate() {
        return create;
    }
}
