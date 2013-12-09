package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;

public class IndexedPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private PrimitivePropertyMethodMetadata propertyMethodMetadata;

    private boolean create;

    protected IndexedPropertyMethodMetadata(PropertyMethod beanPropertyMethod, PrimitivePropertyMethodMetadata propertyMethodMetadata, boolean create, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod, datastoreMetadata);
        this.propertyMethodMetadata = propertyMethodMetadata;
        this.create = create;
    }

    public <IndexedDatastoreMetadata> PrimitivePropertyMethodMetadata<IndexedDatastoreMetadata> getPropertyMethodMetadata() {
        return propertyMethodMetadata;
    }

    public boolean isCreate() {
        return create;
    }
}
