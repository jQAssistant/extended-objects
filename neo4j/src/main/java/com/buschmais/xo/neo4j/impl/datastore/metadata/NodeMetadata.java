package com.buschmais.xo.neo4j.impl.datastore.metadata;

import com.buschmais.xo.neo4j.api.Neo4jLabel;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;

public class NodeMetadata implements DatastoreEntityMetadata<Neo4jLabel> {

    private final Neo4jLabel label;

    private final IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf;

    public NodeMetadata(Neo4jLabel label, IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf) {
        this.label = label;
        this.usingIndexedPropertyOf = usingIndexedPropertyOf;
    }

    @Override
    public Neo4jLabel getDiscriminator() {
        return label;
    }

    public IndexedPropertyMethodMetadata<IndexedPropertyMetadata> getUsingIndexedPropertyOf() {
        return usingIndexedPropertyOf;
    }

}
