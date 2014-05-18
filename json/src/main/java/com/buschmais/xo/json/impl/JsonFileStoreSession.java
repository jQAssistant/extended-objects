package com.buschmais.xo.json.impl;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.json.impl.metadata.JsonNodeMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JsonFileStoreSession implements DatastoreSession<UUID, ObjectNode, JsonNodeMetadata, String, Long, JsonRelation, JsonRelationMetadata, String> {

    private static final String ID_PROPERTY = "id";
    private static final String TYPES_PROPERTY = "types";

    private final ObjectMapper mapper = new ObjectMapper();

    private final File directory;

    public JsonFileStoreSession(File directory) {
        this.directory = directory;
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return new JsonFileStoreTransaction();
    }

    @Override
    public boolean isEntity(Object o) {
        return JsonNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public boolean isRelation(Object o) {
        return JsonRelation.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<String> getEntityDiscriminators(ObjectNode jsonNodes) {
        ArrayNode typesNode = (ArrayNode) jsonNodes.get(TYPES_PROPERTY);
        Set<String> discriminators = new HashSet<>();
        for (JsonNode jsonNode : typesNode) {
            discriminators.add(jsonNode.getTextValue());
        }

        return discriminators;
    }

    @Override
    public String getRelationDiscriminator(JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public UUID getEntityId(ObjectNode jsonNode) {
        return UUID.fromString(jsonNode.get(ID_PROPERTY).asText());
    }

    @Override
    public Long getRelationId(JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public ObjectNode createEntity(TypeMetadataSet<EntityTypeMetadata<JsonNodeMetadata>> types, Set<String> discriminators) {
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode typesNode = mapper.createArrayNode();
        for (String typeName : discriminators) {
            typesNode.add(typeName);
        }
        rootNode.put(TYPES_PROPERTY, typesNode);
        UUID uuid = UUID.randomUUID();
        rootNode.put(ID_PROPERTY, uuid.toString());
        return rootNode;
    }

    @Override
    public void deleteEntity(ObjectNode entity) {
        File file = getFile(entity);
        if (!file.exists()) {
            throw new XOException("Cannot deleteEntity entity '" + entity + "' as it does not exist.");
        }
        file.delete();
    }

    @Override
    public ResultIterator<ObjectNode> findEntity(EntityTypeMetadata<JsonNodeMetadata> type, String discriminator, Object value) {
        return null;
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
    public void migrateEntity(ObjectNode jsonNode, TypeMetadataSet<EntityTypeMetadata<JsonNodeMetadata>> types, Set<String> discriminators, TypeMetadataSet<EntityTypeMetadata<JsonNodeMetadata>> targetTypes, Set<String> targetDiscriminators) {
    }

    @Override
    public void flushEntity(ObjectNode objectNode) {
        File file = getFile(objectNode);
        try {
            mapper.writeValue(new FileWriter(file), objectNode);
        } catch (IOException e) {
            throw new XOException("Cannot write file " + file.getName(), e);
        }
    }

    @Override
    public void flushRelation(JsonRelation jsonRelation) {
    }

    @Override
    public DatastorePropertyManager getDatastorePropertyManager() {
        return new JsonFileStorePropertyManager();
    }

    @Override
    public void close() {
    }

    /**
     * Return the file for the given root object node.
     *
     * @param objectNode The object node.
     * @return The file.
     */
    private File getFile(ObjectNode objectNode) {
        String id = getEntityId(objectNode).toString();
        return new File(directory, id + ".json");
    }

}
