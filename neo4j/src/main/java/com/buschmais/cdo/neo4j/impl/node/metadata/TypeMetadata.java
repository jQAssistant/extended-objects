package com.buschmais.cdo.neo4j.impl.node.metadata;

import org.neo4j.graphdb.Label;

import java.util.Collection;
import java.util.Set;

public class TypeMetadata<DatastoreMetadata> extends AbstractMetadata<DatastoreMetadata> {

    private Class<?> type;
    private IndexedPropertyMethodMetadata indexedProperty;

    public TypeMetadata(Class<?> type, Collection<AbstractMethodMetadata> properties, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(properties, datastoreMetadata);
        this.type = type;
        this.indexedProperty = indexedProperty;
    }

    public Class<?> getType() {
        return type;
    }

    public IndexedPropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }
}
