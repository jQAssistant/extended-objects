package com.buschmais.cdo.json.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.json.impl.metadata.JsonEnumPropertyMetadata;
import com.buschmais.cdo.json.impl.metadata.JsonPrimitivePropertyMetadata;
import com.buschmais.cdo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.metadata.method.EnumPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import org.codehaus.jackson.node.ObjectNode;

public class JsonFileStorePropertyManager implements DatastorePropertyManager<ObjectNode, JsonRelation, JsonPrimitivePropertyMetadata, JsonEnumPropertyMetadata, JsonRelationMetadata> {

    @Override
    public void setEntityProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata, Object value) {
        Class<?> type = metadata.getAnnotatedMethod().getType();
        if (String.class.equals(type)) {
            objectNode.put(metadata.getAnnotatedMethod().getName(), (String) value);
        } else {
            throw new CdoException("Unsupported type " + type + " for property " + metadata.getAnnotatedMethod().getName());
        }
    }

    @Override
    public void setRelationProperty(JsonRelation entity, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata, Object value) {
        // TODO
    }

    @Override
    public boolean hasProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return objectNode.has(metadata.getAnnotatedMethod().getName());
    }

    @Override
    public boolean hasRelationProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return false;
    }

    @Override
    public void removeProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        objectNode.remove(metadata.getAnnotatedMethod().getName());
    }

    @Override
    public void removeRelationProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {

    }

    @Override
    public Object getProperty(ObjectNode objectNode, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return objectNode.get(metadata.getAnnotatedMethod().getName());
    }

    @Override
    public Object getRelationProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return null;
    }

    @Override
    public Enum<?> getEnumProperty(ObjectNode objectNode, EnumPropertyMethodMetadata<JsonEnumPropertyMetadata> metadata) {
        return null;
    }

    @Override
    public Enum<?> getRelationEnumProperty(JsonRelation jsonRelation, EnumPropertyMethodMetadata<JsonEnumPropertyMetadata> metadata) {
        return null;
    }

    @Override
    public void setEnumProperty(ObjectNode objectNode, EnumPropertyMethodMetadata<JsonEnumPropertyMetadata> metadata, Enum<?> value) {

    }

    @Override
    public void setRelationEnumProperty(JsonRelation jsonRelation, EnumPropertyMethodMetadata<JsonEnumPropertyMetadata> metadata, Enum<?> value) {

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
    public ObjectNode getTarget(JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public ObjectNode getSource(JsonRelation jsonRelation) {
        return null;
    }
}
