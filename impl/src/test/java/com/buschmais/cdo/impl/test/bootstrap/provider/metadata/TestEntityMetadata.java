package com.buschmais.cdo.impl.test.bootstrap.provider.metadata;

import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;

public class TestEntityMetadata implements DatastoreEntityMetadata<String> {

    private final String type;

    public TestEntityMetadata(String type) {
        this.type = type;
    }

    @Override
    public String getDiscriminator() {
        return null;
    }
}
