package com.buschmais.cdo.neo4j.impl.datastore.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.metadata.method.IndexedPropertyMethodMetadata;
import org.neo4j.graphdb.Label;

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
