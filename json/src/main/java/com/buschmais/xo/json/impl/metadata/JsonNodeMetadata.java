package com.buschmais.xo.json.impl.metadata;

import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;

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
