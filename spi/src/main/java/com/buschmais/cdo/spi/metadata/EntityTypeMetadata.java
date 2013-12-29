package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public class EntityTypeMetadata<DatastoreMetadata extends DatastoreEntityMetadata<?>> extends AbstractMetadata<EntityTypeMetadata<DatastoreMetadata>, DatastoreMetadata> {

    private IndexedPropertyMethodMetadata indexedProperty;

    public EntityTypeMetadata(AnnotatedType annotatedType, Collection<EntityTypeMetadata<DatastoreMetadata>> superEntityTypeMetadatas, Collection<MethodMetadata> methodMetadatas, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superEntityTypeMetadatas, methodMetadatas, datastoreMetadata);
        this.indexedProperty = indexedProperty;
    }

    public IndexedPropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }
}
