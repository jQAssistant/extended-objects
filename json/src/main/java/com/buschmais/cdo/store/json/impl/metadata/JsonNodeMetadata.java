package com.buschmais.cdo.store.json.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;

import java.util.Collection;

public class JsonNodeMetadata implements DatastoreEntityMetadata<String> {

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

    @Override
    public String getDiscriminator() {
        return null;
    }
}
