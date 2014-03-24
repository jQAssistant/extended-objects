package com.buschmais.xo.impl.test.bootstrap.provider.metadata;

import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;

public class TestEntityMetadata implements DatastoreEntityMetadata<String> {

    private final String type;

    public TestEntityMetadata(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getDiscriminator() {
        return null;
    }
}
