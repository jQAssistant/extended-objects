package com.buschmais.cdo.neo4j.impl.metadata;

import org.neo4j.graphdb.Label;

import java.util.Map;
import java.util.Set;

public class NodeMetadata {

    private Class<?> type;
    private Set<NodeMetadata> superNodes;
    private Label label;
    private Set<Label> aggregatedLabels;
    private Map<String, AbstractPropertyMetadata> properties;
    private PrimitivePropertyMetadata indexedProperty;

    public NodeMetadata(Class<?> type, Set<NodeMetadata> superNodeMetadataSet, Label label, Set<Label> aggregatedLabels, Map<String, AbstractPropertyMetadata> properties, PrimitivePropertyMetadata indexedProperty) {
        this.type = type;
        this.superNodes = superNodeMetadataSet;
        this.label = label;
        this.aggregatedLabels = aggregatedLabels;
        this.properties = properties;
        this.indexedProperty = indexedProperty;
    }

    public Class<?> getType() {
        return type;
    }

    public Set<NodeMetadata> getSuperNodes() {
        return superNodes;
    }

    public Label getLabel() {
        return label;
    }

    public Set<Label> getAggregatedLabels() {
        return aggregatedLabels;
    }

    public Map<String, AbstractPropertyMetadata> getProperties() {
        return properties;
    }

    public PrimitivePropertyMetadata getIndexedProperty() {
        return indexedProperty;
    }
}
