package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.PropertyMethod;

public class IndexedPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private PrimitivePropertyMethodMetadata propertyMethodMetadata;

    public IndexedPropertyMethodMetadata(PropertyMethod beanPropertyMethod, PrimitivePropertyMethodMetadata propertyMethodMetadata, DatastoreMetadata datastoreMetadata) {
        super(beanPropertyMethod, datastoreMetadata);
        this.propertyMethodMetadata = propertyMethodMetadata;
    }

    public <IndexedDatastoreMetadata> PrimitivePropertyMethodMetadata<IndexedDatastoreMetadata> getPropertyMethodMetadata() {
        return propertyMethodMetadata;
    }

}
