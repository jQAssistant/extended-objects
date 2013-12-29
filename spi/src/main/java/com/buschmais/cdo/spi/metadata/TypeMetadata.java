package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public class TypeMetadata<DatastoreMetadata extends DatastoreEntityMetadata<?>> extends AbstractMetadata<TypeMetadata<DatastoreMetadata>, DatastoreMetadata> {

    private final IndexedPropertyMethodMetadata indexedProperty;

    public TypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata<DatastoreMetadata>> superTypeMetadatas, Collection<AbstractMethodMetadata> methodMetadatas, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypeMetadatas, methodMetadatas, datastoreMetadata);
        this.indexedProperty = indexedProperty;
    }


    public IndexedPropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }
}
