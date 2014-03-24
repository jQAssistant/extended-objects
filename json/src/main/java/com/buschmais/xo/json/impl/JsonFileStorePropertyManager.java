package com.buschmais.xo.json.impl;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.json.impl.metadata.JsonPrimitivePropertyMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import org.codehaus.jackson.node.ObjectNode;

public class JsonFileStorePropertyManager implements DatastorePropertyManager<ObjectNode, JsonRelation, JsonPrimitivePropertyMetadata, JsonRelationMetadata> {

    @Override
    public void setEntityProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata, Object value) {
        Class<?> type = metadata.getAnnotatedMethod().getType();
        if (String.class.equals(type)) {
            objectNode.put(metadata.getAnnotatedMethod().getName(), (String) value);
        } else {
            throw new XOException("Unsupported type " + type + " for property " + metadata.getAnnotatedMethod().getName());
        }
    }

    @Override
    public void setRelationProperty(JsonRelation entity, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata, Object value) {
        // TODO
    }

    @Override
    public boolean hasEntityProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return objectNode.has(metadata.getAnnotatedMethod().getName());
    }

    @Override
    public boolean hasRelationProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return false;
    }

    @Override
    public void removeEntityProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        objectNode.remove(metadata.getAnnotatedMethod().getName());
    }

    @Override
    public void removeRelationProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {

    }

    @Override
    public Object getEntityProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return objectNode.get(metadata.getAnnotatedMethod().getName());
    }

    @Override
    public Object getRelationProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return null;
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
    public JsonRelation createRelation(ObjectNode source, RelationTypeMetadata<JsonRelationMetadata> metadata, RelationTypeMetadata.Direction direction, ObjectNode target) {
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
}
