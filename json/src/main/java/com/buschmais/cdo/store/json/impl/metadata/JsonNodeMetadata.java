package com.buschmais.cdo.store.json.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;

import java.util.Collection;

public class JsonNodeMetadata implements DatastoreEntityMetadata<String> {

    private String typeProperty;

    public JsonNodeMetadata(String typeProperty) {
        this.typeProperty = typeProperty;
    }

    public String getTypeProperty() {
        return typeProperty;
    }

    @Override
    public String getDiscriminator() {
        return null;
    }
}
