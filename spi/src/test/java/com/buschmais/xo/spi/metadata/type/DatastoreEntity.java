package com.buschmais.xo.spi.metadata.type;

import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;

public class DatastoreEntity implements DatastoreEntityMetadata<String> {

    private String discriminator;

    public DatastoreEntity(String discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public String getDiscriminator() {
        return discriminator;
    }
}
