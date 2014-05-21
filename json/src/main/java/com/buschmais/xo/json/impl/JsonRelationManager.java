package com.buschmais.xo.json.impl;

import com.buschmais.xo.json.impl.metadata.JsonPrimitivePropertyMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Created by dimahler on 5/21/2014.
 */
public class JsonRelationManager implements DatastoreRelationManager<ObjectNode, Long, JsonRelation, JsonRelationMetadata, String, JsonPrimitivePropertyMetadata> {

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
    public void flushRelation(JsonRelation jsonRelation) {
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

    @Override
    public void setProperty(JsonRelation entity, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata, Object value) {
    }

    @Override
    public boolean hasProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return false;
    }

    @Override
    public void removeProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
    }

    @Override
    public Object getProperty(JsonRelation jsonRelation, PrimitivePropertyMethodMetadata<JsonPrimitivePropertyMetadata> metadata) {
        return null;
    }
}
