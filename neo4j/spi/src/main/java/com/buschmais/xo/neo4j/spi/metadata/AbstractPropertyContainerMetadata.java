package com.buschmais.xo.neo4j.spi.metadata;

abstract class AbstractPropertyContainerMetadata {

    private boolean batchable;

    protected AbstractPropertyContainerMetadata(boolean batchable) {
        this.batchable = batchable;
    }

    public boolean isBatchable() {
        return batchable;
    }
}
