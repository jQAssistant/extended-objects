package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public abstract class AbstractDatastoreTypeMetadata<DatastoreMetadata> extends AbstractTypeMetadata implements DatastoreTypeMetadata<DatastoreMetadata> {

    private final DatastoreMetadata datastoreMetadata;

    protected AbstractDatastoreTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata> superTypes, Collection<MethodMetadata<?, ?>> properties, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(annotatedType, superTypes, properties, indexedProperty);
        this.datastoreMetadata = datastoreMetadata;
    }

    @Override
    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

}
