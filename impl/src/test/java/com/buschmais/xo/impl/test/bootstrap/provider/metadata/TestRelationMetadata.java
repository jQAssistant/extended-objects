package com.buschmais.xo.impl.test.bootstrap.provider.metadata;

import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

public class TestRelationMetadata implements DatastoreRelationMetadata<String> {

    @Override
    public String getDiscriminator() {
        return null;
    }

}
