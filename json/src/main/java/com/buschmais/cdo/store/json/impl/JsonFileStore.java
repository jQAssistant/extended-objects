package com.buschmais.cdo.store.json.impl;

import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.cdo.spi.metadata.EntityTypeMetadata;
import com.buschmais.cdo.store.json.impl.metadata.JsonNodeMetadata;

import java.io.File;
import java.util.Collection;

public class JsonFileStore implements Datastore<JsonFileStoreSession, JsonNodeMetadata, String> {

    private File directory;

    public JsonFileStore(String directory) {
        this.directory = new File(directory);
        this.directory.mkdirs();
    }

    @Override
    public DatastoreMetadataFactory<JsonNodeMetadata, String> getMetadataFactory() {
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
    public void init(Collection<EntityTypeMetadata<JsonNodeMetadata>> registeredMetadata) {
    }
}
