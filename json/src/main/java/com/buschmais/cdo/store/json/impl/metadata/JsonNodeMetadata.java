package com.buschmais.cdo.store.json.impl.metadata;

import java.util.Collection;

public class JsonNodeMetadata {

    private String typeProperty;
    private Collection<String> aggregatedTypeNames;

    public JsonNodeMetadata(String typeProperty, Collection<String> aggregatedTypeNames) {
        this.typeProperty = typeProperty;
        this.aggregatedTypeNames = aggregatedTypeNames;
    }

    public Collection<String> getAggregatedTypeNames() {
        return aggregatedTypeNames;
    }

    public String getTypeProperty() {
        return typeProperty;
    }
}
