package com.buschmais.xo.neo4j.impl.datastore.metadata;

import org.neo4j.graphdb.Label;

import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;

public class NodeMetadata implements DatastoreEntityMetadata<Label> {

    private final Label label;

    private final IndexedPropertyMethodMetadata<?> indexedProperty;

    public NodeMetadata(Label label, IndexedPropertyMethodMetadata<?> indexedProperty) {
        this.label = label;
        this.indexedProperty = indexedProperty;
    }

    @Override
    public Label getDiscriminator() {
        return label;
    }

    public IndexedPropertyMethodMetadata<?> getIndexedProperty() {
        return indexedProperty;
    }

}
