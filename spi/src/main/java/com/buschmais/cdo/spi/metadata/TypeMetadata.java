package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public class TypeMetadata<DatastoreMetadata extends DatastoreEntityMetadata<?>> extends AbstractMetadata<DatastoreMetadata> {

    private AnnotatedType annotatedType;
    private IndexedPropertyMethodMetadata indexedProperty;

    public TypeMetadata(AnnotatedType annotatedType, Collection<AbstractMethodMetadata> properties, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(properties, datastoreMetadata);
        this.annotatedType = annotatedType;
        this.indexedProperty = indexedProperty;
    }

    public AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    public IndexedPropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }

    @Override
    public String toString() {
        return "TypeMetadata{" +
                "type=" + annotatedType +
                '}';
    }
}
