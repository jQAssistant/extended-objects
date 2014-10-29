package com.buschmais.xo.json.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.json.impl.metadata.JsonNodeMetadata;
import com.buschmais.xo.json.impl.metadata.JsonPropertyMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.session.XOSession;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.UUID;

public class JsonDatastoreSession implements
        DatastoreSession<UUID, ObjectNode, JsonNodeMetadata, String, Long, JsonRelation, JsonRelationMetadata, String, JsonPropertyMetadata> {

    private final JsonEntityManager entityManager;
    private final JsonRelationManager relationManager;

    public JsonDatastoreSession(File directory) {
        this.entityManager = new JsonEntityManager(directory);
        this.relationManager = new JsonRelationManager();
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return null;
    }

    @Override
    public DatastoreEntityManager<UUID, ObjectNode, JsonNodeMetadata, String, JsonPropertyMetadata> getDatastoreEntityManager() {
        return entityManager;
    }

    @Override
    public DatastoreRelationManager<ObjectNode, Long, JsonRelation, JsonRelationMetadata, String, JsonPropertyMetadata> getDatastoreRelationManager() {
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
    public <R> R createRepository(XOSession xoSession, Class<R> type) {
        throw new XOException("Repositories are not supported");
    }

    @Override
    public void close() {
    }
}
