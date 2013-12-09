package com.buschmais.cdo.neo4j.impl.node.metadata;

import org.neo4j.graphdb.Label;

import java.util.Collection;
import java.util.Set;

public class EntityMetadata<DatastoreMetadata> extends AbstractMetadata<DatastoreMetadata> {

    private Class<?> type;
    private Label label;
    private Set<Label> aggregatedLabels;
    private IndexedPropertyMethodMetadata indexedProperty;

    public EntityMetadata(Class<?> type, Label label, Set<Label> aggregatedLabels, Collection<AbstractMethodMetadata> properties, IndexedPropertyMethodMetadata indexedProperty, DatastoreMetadata datastoreMetadata) {
        super(properties, datastoreMetadata);
        this.type = type;
        this.label = label;
        this.aggregatedLabels = aggregatedLabels;
        this.indexedProperty = indexedProperty;
    }

    public Class<?> getType() {
        return type;
    }

    public Label getLabel() {
        return label;
    }

    public Collection<Label> getAggregatedLabels() {
        return aggregatedLabels;
    }

    public IndexedPropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }
}
