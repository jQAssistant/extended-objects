package com.buschmais.cdo.neo4j.impl.node.metadata;

import org.neo4j.graphdb.Label;

import java.util.Collection;
import java.util.Set;

public class NodeMetadata extends AbstractMetadata {

    private Class<?> type;
    private Label label;
    private Set<Label> aggregatedLabels;
    private IndexedPropertyMethodMetadata indexedProperty;

    public NodeMetadata(Class<?> type, Label label, Set<Label> aggregatedLabels, Collection<AbstractMethodMetadata> properties, IndexedPropertyMethodMetadata indexedProperty) {
        super(properties);
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
