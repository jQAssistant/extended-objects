package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public class EntityTypeMetadata<DatastoreMetadata extends DatastoreEntityMetadata<?>> extends AbstractDatastoreTypeMetadata<DatastoreMetadata> {

    public EntityTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?,?>> properties, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypes, properties, indexedProperty, datastoreMetadata);
    }
}
