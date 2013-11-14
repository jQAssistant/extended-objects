package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.Label;

import java.util.Collection;
import java.util.Set;

public class NodeMetadata {

    private Class<?> type;
    private Label label;
    private Set<Label> aggregatedLabels;
    private Collection<AbstractMethodMetadata> properties;
    private PrimitivePropertyMethodMetadata indexedProperty;

    public NodeMetadata(Class<?> type, Label label, Set<Label> aggregatedLabels, Collection<AbstractMethodMetadata> properties, PrimitivePropertyMethodMetadata indexedProperty) {
        this.type = type;
        this.label = label;
        this.aggregatedLabels = aggregatedLabels;
        this.properties = properties;
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

    public Collection<AbstractMethodMetadata> getProperties() {
        return properties;
    }

    public PrimitivePropertyMethodMetadata getIndexedProperty() {
        return indexedProperty;
    }
}
