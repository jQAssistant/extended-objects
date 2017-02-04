package com.buschmais.xo.neo4j.embedded.impl.datastore.metadata;

import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;

public class NodeMetadata implements DatastoreEntityMetadata<EmbeddedLabel> {

    private final EmbeddedLabel label;

    private final IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf;

    public NodeMetadata(EmbeddedLabel label, IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf) {
        this.label = label;
        this.usingIndexedPropertyOf = usingIndexedPropertyOf;
    }

    @Override
    public EmbeddedLabel getDiscriminator() {
        return label;
    }

    public IndexedPropertyMethodMetadata<IndexedPropertyMetadata> getUsingIndexedPropertyOf() {
        return usingIndexedPropertyOf;
    }

}
