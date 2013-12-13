package com.buschmais.cdo.store.json.impl;

import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.metadata.EnumPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.RelationMetadata;
import com.buschmais.cdo.store.json.impl.metadata.JsonEnumPropertyMetadata;
import com.buschmais.cdo.store.json.impl.metadata.JsonPrimitivePropertyMetadata;
import com.buschmais.cdo.store.json.impl.metadata.JsonRelationPropertyMetadata;
import org.codehaus.jackson.node.ObjectNode;

public class JsonFileDatastorePropertyManager implements DatastorePropertyManager<ObjectNode, JsonRelation, JsonPrimitivePropertyMetadata, JsonEnumPropertyMetadata, JsonRelationPropertyMetadata> {

    @Override
    public void setProperty(ObjectNode jsonNodes, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata, Object value) {

    }

    @Override
    public boolean hasProperty(ObjectNode jsonNodes, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return false;
    }

    @Override
    public void removeProperty(ObjectNode jsonNodes, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {

    }

    @Override
    public Object getProperty(ObjectNode jsonNodes, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return null;
    }

    @Override
    public Enum<?> getEnumProperty(ObjectNode jsonNodes, EnumPropertyMethodMetadata<JsonEnumPropertyMetadata> metadata) {
        return null;
    }

    @Override
    public void setEnumProperty(ObjectNode jsonNodes, EnumPropertyMethodMetadata<JsonEnumPropertyMetadata> metadata, Object value) {

    }

    @Override
    public boolean hasRelation(ObjectNode source, RelationMetadata<JsonRelationPropertyMetadata> metadata, RelationMetadata.Direction direction) {
        return false;
    }

    @Override
    public JsonRelation getSingleRelation(ObjectNode source, RelationMetadata<JsonRelationPropertyMetadata> metadata, RelationMetadata.Direction direction) {
        return null;
    }

    @Override
    public Iterable<JsonRelation> getRelations(ObjectNode source, RelationMetadata<JsonRelationPropertyMetadata> metadata, RelationMetadata.Direction direction) {
        return null;
    }

    @Override
    public JsonRelation createRelation(ObjectNode source, RelationMetadata<JsonRelationPropertyMetadata> metadata, RelationMetadata.Direction direction, ObjectNode target) {
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
