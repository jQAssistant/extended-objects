package com.buschmais.cdo.store.json.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;

import java.util.Collection;

public class JsonNodeMetadata implements DatastoreEntityMetadata<String> {

    private final String discriminator;

    public JsonNodeMetadata(String discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public String getDiscriminator() {
        return discriminator;
    }
}
