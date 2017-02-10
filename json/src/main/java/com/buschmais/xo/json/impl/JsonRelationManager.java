package com.buschmais.xo.json.impl;

import java.util.Map;

import org.codehaus.jackson.node.ObjectNode;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.json.impl.metadata.JsonPropertyMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

public class JsonRelationManager implements DatastoreRelationManager<ObjectNode, Long, JsonRelation, JsonRelationMetadata, String, JsonPropertyMetadata> {

    @Override
    public boolean isRelation(Object o) {
        return JsonRelation.class.isAssignableFrom(o.getClass());
    }

    @Override
    public String getRelationDiscriminator(JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public Long getRelationId(JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public JsonRelation findRelationById(RelationTypeMetadata<JsonRelationMetadata> metadata, Long aLong) {
        throw new XOException("Not supported");
    }

    @Override
    public boolean hasSingleRelation(ObjectNode source, RelationTypeMetadata<JsonRelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return false;
    }

    @Override
    public JsonRelation getSingleRelation(ObjectNode source, RelationTypeMetadata<JsonRelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return null;
    }

    @Override
    public Iterable<JsonRelation> getRelations(ObjectNode source, RelationTypeMetadata<JsonRelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return null;
    }

    @Override
    public JsonRelation createRelation(ObjectNode source, RelationTypeMetadata<JsonRelationMetadata> metadata, RelationTypeMetadata.Direction direction,
            ObjectNode target, Map<PrimitivePropertyMethodMetadata<JsonPropertyMetadata>, Object> example) {
        return null;
    }

    @Override
    public void deleteRelation(JsonRelation jsonRelation) {
    }

    @Override
    public ObjectNode getTo(JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public ObjectNode getFrom(JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public void setProperty(JsonRelation entity, PrimitivePropertyMethodMetadata<JsonPropertyMetadata> metadata, Object value) {
    }

    @Override
    public boolean hasProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPropertyMetadata> metadata) {
        return false;
    }

    @Override
    public void removeProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPropertyMetadata> metadata) {
    }

    @Override
    public Object getProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPropertyMetadata> metadata) {
        return null;
    }

    @Override
    public void flush(Iterable<JsonRelation> entities) {
    }

    @Override
    public void clear(Iterable<JsonRelation> entities) {
    }
}
