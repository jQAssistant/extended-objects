package com.buschmais.xo.json.impl;

import com.buschmais.xo.json.impl.metadata.JsonNodeMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

import java.io.File;
import java.util.Collection;

public class JsonFileStore implements Datastore<JsonDatastoreSession, JsonNodeMetadata, String, JsonRelationMetadata, String> {

    private final File directory;

    public JsonFileStore(String directory) {
        this.directory = new File(directory);
        this.directory.mkdirs();
    }

    @Override
    public DatastoreMetadataFactory<JsonNodeMetadata, String, JsonRelationMetadata, String> getMetadataFactory() {
        return new JsonMetadataFactory();
    }

    @Override
    public JsonDatastoreSession createSession() {
        return new JsonDatastoreSession(directory);
    }

    @Override
    public void close() {
    }

    @Override
    public void init(Collection<TypeMetadata> registeredMetadata) {
    }
}
