package com.buschmais.cdo.json.impl;

import com.buschmais.cdo.json.impl.metadata.JsonNodeMetadata;
import com.buschmais.cdo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;

import java.io.File;
import java.util.Collection;

public class JsonFileStore implements Datastore<JsonFileStoreSession, JsonNodeMetadata, String, JsonRelationMetadata, String> {

    private File directory;

    public JsonFileStore(String directory) {
        this.directory = new File(directory);
        this.directory.mkdirs();
    }

    @Override
    public DatastoreMetadataFactory<JsonNodeMetadata, String, JsonRelationMetadata, String> getMetadataFactory() {
        return new JsonMetadataFactory();
    }

    @Override
    public JsonFileStoreSession createSession() {
        return new JsonFileStoreSession(directory);
    }

    @Override
    public void close() {
    }

    @Override
    public void init(Collection<TypeMetadata> registeredMetadata) {
    }
}
