package com.buschmais.xo.neo4j.spi.metadata;

import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;

public class NodeMetadata<L extends Neo4jLabel> implements DatastoreEntityMetadata<L> {

    private final L label;

    private final IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf;

    public NodeMetadata(L label, IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf) {
        this.label = label;
        this.usingIndexedPropertyOf = usingIndexedPropertyOf;
    }

    @Override
    public L getDiscriminator() {
        return label;
    }

    public IndexedPropertyMethodMetadata<IndexedPropertyMetadata> getUsingIndexedPropertyOf() {
        return usingIndexedPropertyOf;
    }

}
