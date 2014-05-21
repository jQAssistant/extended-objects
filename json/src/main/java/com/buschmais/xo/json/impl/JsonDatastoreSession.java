package com.buschmais.xo.json.impl;

import com.buschmais.xo.json.impl.metadata.JsonNodeMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.*;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.UUID;

public class JsonDatastoreSession implements DatastoreSession<UUID, ObjectNode, JsonNodeMetadata, String, Long, JsonRelation, JsonRelationMetadata, String> {


    private final JsonDatastoreEntityManager entityManager;
    private final JsonDatastoreRelationManager relationManager;

    public JsonDatastoreSession(File directory) {
        this.entityManager = new JsonDatastoreEntityManager(directory);
        this.relationManager = new JsonDatastoreRelationManager();
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return new JsonFileStoreTransaction();
    }

    @Override
    public DatastoreEntityManager<UUID, ObjectNode, JsonNodeMetadata, String, ?> getDatastoreEntityManager() {
        return entityManager;
    }

    @Override
    public DatastoreRelationManager<ObjectNode, Long, JsonRelation, JsonRelationMetadata, String, ?> getDatastoreRelationManager() {
        return relationManager;
    }

    @Override
    public Class<? extends Annotation> getDefaultQueryLanguage() {
        return null;
    }

    @Override
    public <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage) {
        return null;
    }

    @Override
    public void close() {
    }


}
