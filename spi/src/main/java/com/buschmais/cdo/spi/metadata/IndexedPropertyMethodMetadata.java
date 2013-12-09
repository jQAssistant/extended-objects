package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

public class IndexedPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private PrimitivePropertyMethodMetadata propertyMethodMetadata;

    private boolean create;

    public IndexedPropertyMethodMetadata(PropertyMethod beanPropertyMethod, PrimitivePropertyMethodMetadata propertyMethodMetadata, boolean create, DatastoreMetadata datastoreMetadata) {
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
