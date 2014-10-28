package com.buschmais.xo.spi.metadata.type;

import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedType;

import java.util.Collection;

/**
 * Abstract base implementation for metadata representing a datastore type.
 *
 * @param <DatastoreMetadata> The datastore metadate type.
 */
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
