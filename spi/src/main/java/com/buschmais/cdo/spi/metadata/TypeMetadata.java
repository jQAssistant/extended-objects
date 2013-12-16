package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;

import java.util.Collection;

public class TypeMetadata<DatastoreMetadata extends DatastoreEntityMetadata<?>> extends AbstractMetadata<DatastoreMetadata> {

    private Class<?> type;
    private IndexedPropertyMethodMetadata indexedProperty;

    public TypeMetadata(Class<?> type, Collection<AbstractMethodMetadata> properties, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(properties, datastoreMetadata);
        this.type = type;
        this.indexedProperty = indexedProperty;
    }

    public Class<?> getType() {
        return type;
    }

    public IndexedPropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }

    @Override
    public String toString() {
        return "TypeMetadata{" +
                "type=" + type +
                '}';
    }
}
