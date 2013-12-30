package com.buschmais.cdo.spi.metadata.method;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

public class IndexedPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private PrimitivePropertyMethodMetadata propertyMethodMetadata;

    public IndexedPropertyMethodMetadata(PropertyMethod propertyMethod, PrimitivePropertyMethodMetadata propertyMethodMetadata, DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
        this.propertyMethodMetadata = propertyMethodMetadata;
    }

    public <IndexedDatastoreMetadata> PrimitivePropertyMethodMetadata<IndexedDatastoreMetadata> getPropertyMethodMetadata() {
        return propertyMethodMetadata;
    }

}
