package com.buschmais.cdo.neo4j.impl.datastore.metadata;

import com.buschmais.cdo.neo4j.impl.node.metadata.IndexedPropertyMethodMetadata;
import org.neo4j.graphdb.Label;

import java.util.Set;

public class NodeMetadata {

    private Label label;

    private Set<Label> aggregatedLabels;

    private IndexedPropertyMethodMetadata<?> indexedProperty;

    public NodeMetadata(Label label, Set<Label> aggregatedLabels, IndexedPropertyMethodMetadata<?> indexedProperty) {
        this.label = label;
        this.aggregatedLabels = aggregatedLabels;
        this.indexedProperty = indexedProperty;
    }

    public Label getLabel() {
        return label;
    }

    public Set<Label> getAggregatedLabels() {
        return aggregatedLabels;
    }

    public IndexedPropertyMethodMetadata<?> getIndexedProperty() {
        return indexedProperty;
    }
}
