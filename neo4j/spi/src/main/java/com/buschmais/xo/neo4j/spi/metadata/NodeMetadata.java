package com.buschmais.xo.neo4j.spi.metadata;

import com.buschmais.xo.api.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.DatastoreEntityMetadata;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;

public class NodeMetadata<L extends Neo4jLabel> extends AbstractPropertyContainerMetadata implements DatastoreEntityMetadata<L> {

    private final L label;

    private final IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf;

    public NodeMetadata(L label, IndexedPropertyMethodMetadata<IndexedPropertyMetadata> usingIndexedPropertyOf, boolean batchable) {
        super(batchable);
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
