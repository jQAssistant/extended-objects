package com.buschmais.cdo.neo4j.impl.node.metadata;

import java.util.Collection;

public abstract class AbstractMetadata {

    private Collection<AbstractMethodMetadata> properties;

    protected AbstractMetadata(Collection<AbstractMethodMetadata> properties) {
        this.properties = properties;
    }

    public Collection<AbstractMethodMetadata> getProperties() {
        return properties;
    }

}
