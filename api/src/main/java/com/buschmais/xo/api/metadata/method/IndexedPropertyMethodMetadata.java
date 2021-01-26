package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.PropertyMethod;

public class IndexedPropertyMethodMetadata<DatastoreMetadata> extends AbstractPropertyMethodMetadata<DatastoreMetadata> {

    private final PrimitivePropertyMethodMetadata propertyMethodMetadata;

    public IndexedPropertyMethodMetadata(PropertyMethod propertyMethod, PrimitivePropertyMethodMetadata propertyMethodMetadata,
            DatastoreMetadata datastoreMetadata) {
        super(propertyMethod, datastoreMetadata);
        this.propertyMethodMetadata = propertyMethodMetadata;
    }

    public <IndexedDatastoreMetadata> PrimitivePropertyMethodMetadata<IndexedDatastoreMetadata> getPropertyMethodMetadata() {
        return propertyMethodMetadata;
    }

}
