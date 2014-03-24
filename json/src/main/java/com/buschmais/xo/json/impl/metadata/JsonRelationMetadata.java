package com.buschmais.xo.json.impl.metadata;

import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;

public class JsonRelationMetadata implements DatastoreRelationMetadata<String> {

    @Override
    public String getDiscriminator() {
        return null;
    }

}
