package com.buschmais.xo.json.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.json.impl.metadata.JsonNodeMetadata;
import com.buschmais.xo.json.impl.metadata.JsonRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

public class JsonFileStoreSession implements DatastoreSession<UUID, ObjectNode, JsonNodeMetadata, String, Long, JsonRelation, JsonRelationMetadata, String> {

    private static final String ID_PROPERTY = "id";
    private static final String TYPES_PROPERTY = "types";

    private final ObjectMapper mapper = new ObjectMapper();

    private final File directory;

    public JsonFileStoreSession(final File directory) {
        this.directory = directory;
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return new JsonFileStoreTransaction();
    }

    @Override
    public boolean isEntity(final Object o) {
        return JsonNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public boolean isRelation(final Object o) {
        return JsonRelation.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<String> getEntityDiscriminators(final ObjectNode jsonNodes) {
        final ArrayNode typesNode = (ArrayNode) jsonNodes.get(TYPES_PROPERTY);
        final Set<String> discriminators = new HashSet<>();
        for (final JsonNode jsonNode : typesNode) {
            discriminators.add(jsonNode.getTextValue());
        }

        return discriminators;
    }

    @Override
    public String getRelationDiscriminator(final JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public UUID getEntityId(final ObjectNode jsonNode) {
        return UUID.fromString(jsonNode.get(ID_PROPERTY).asText());
    }

    @Override
    public Long getRelationId(final JsonRelation jsonRelation) {
        return null;
    }

    @Override
    public ObjectNode createEntity(final TypeMetadataSet<EntityTypeMetadata<JsonNodeMetadata>> types, final Set<String> discriminators) {
        final ObjectNode rootNode = mapper.createObjectNode();
        final ArrayNode typesNode = mapper.createArrayNode();
        for (final String typeName : discriminators) {
            typesNode.add(typeName);
        }
        rootNode.put(TYPES_PROPERTY, typesNode);
        final UUID uuid = UUID.randomUUID();
        rootNode.put(ID_PROPERTY, uuid.toString());
        return rootNode;
    }

    @Override
    public void deleteEntity(final ObjectNode entity) {
        final File file = getFile(entity);
        if (!file.exists()) {
            throw new XOException("Cannot deleteEntity entity '" + entity + "' as it does not exist.");
        }
        file.delete();
    }

    @Override
    public ResultIterator<ObjectNode> findEntity(final EntityTypeMetadata<JsonNodeMetadata> type, final String discriminator, final Object value) {
        return null;
    }

    @Override
    public ResultIterator<Map<String,Object>> executeQuery(final NativeQuery query, final java.util.Map<String,Object> parameters) {
        return null;
    };

    @Override
    public <QL> NativeQuery<?> getNativeQuery(final AnnotatedElement expression, final Class<? extends Annotation> language) {
        return null;
    }

    @Override
    public NativeQuery<?> getNativeQuery(final String expression, final Class<? extends Annotation> language) {
        return null;
    }

    @Override
    public void migrateEntity(final ObjectNode jsonNode, final TypeMetadataSet<EntityTypeMetadata<JsonNodeMetadata>> types, final Set<String> discriminators, final TypeMetadataSet<EntityTypeMetadata<JsonNodeMetadata>> targetTypes, final Set<String> targetDiscriminators) {
    }

    @Override
    public void flushEntity(final ObjectNode objectNode) {
        final File file = getFile(objectNode);
        try {
            mapper.writeValue(new FileWriter(file), objectNode);
        } catch (final IOException e) {
            throw new XOException("Cannot write file " + file.getName());
        }
    }

    @Override
    public void flushRelation(final JsonRelation jsonRelation) {
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
    private File getFile(final ObjectNode objectNode) {
        final String id = getEntityId(objectNode).toString();
        return new File(directory, id + ".json");
    }

}
