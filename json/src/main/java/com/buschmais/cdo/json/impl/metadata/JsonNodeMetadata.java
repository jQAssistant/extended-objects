package com.buschmais.cdo.json.impl.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;

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
