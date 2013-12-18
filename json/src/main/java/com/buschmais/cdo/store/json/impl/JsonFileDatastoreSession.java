package com.buschmais.cdo.store.json.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.spi.datastore.DatastorePropertyManager;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.datastore.DatastoreTransaction;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.store.json.impl.metadata.JsonNodeMetadata;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class JsonFileDatastoreSession implements DatastoreSession<UUID, ObjectNode, JsonNodeMetadata, String, Long, JsonRelation> {

    private ObjectMapper mapper = new ObjectMapper();

    private File directory;

    public JsonFileDatastoreSession(File directory) {
        this.directory = directory;
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return new JsonFileDatastoreTransaction();
    }

    @Override
    public boolean isEntity(Object o) {
        return JsonNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<String> getDiscriminators(ObjectNode jsonNodes) {
        return null;
    }

    @Override
    public UUID getId(ObjectNode jsonNode) {
        return UUID.fromString(jsonNode.get("id").asText());
    }

    @Override
    public ObjectNode create(TypeMetadataSet<JsonNodeMetadata> types, Set<String> discriminators) {
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode typesNode = mapper.createArrayNode();
        String typePropertyName = null;
        for (TypeMetadata<JsonNodeMetadata> typeMetadata : types) {
            JsonNodeMetadata datastoreMetadata = typeMetadata.getDatastoreMetadata();
            typePropertyName = datastoreMetadata.getTypeProperty();
            for (String typeName : discriminators) {
                typesNode.add(typeName);
            }
        }
        rootNode.put(typePropertyName, typesNode);
        UUID uuid = UUID.randomUUID();
        rootNode.put("id", uuid.toString());
        return rootNode;
    }

    @Override
    public void delete(ObjectNode entity) {

    }

    @Override
    public ResultIterator<ObjectNode> find(TypeMetadata<JsonNodeMetadata> type, String discriminator, Object value) {
        return null;
    }

    @Override
    public <QL> ResultIterator<Map<String, Object>> execute(QL query, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public void migrate(ObjectNode jsonNode, TypeMetadataSet<JsonNodeMetadata> types, Set<String> discriminators, TypeMetadataSet<JsonNodeMetadata> targetTypes, Set<String> targetDiscriminators) {

    }

    @Override
    public void flush(Iterable<ObjectNode> objectNodes) {
        for (ObjectNode objectNode : objectNodes) {
            String id = getId(objectNode).toString();
            File file = new File(directory, id + ".com.buschmais.cdo.store.json");
            try {
                mapper.writeValue(new FileWriter(file), objectNode);
            } catch (IOException e) {
                throw new CdoException("Cannot write file " + file.getName());
            }
        }

    }

    @Override
    public DatastorePropertyManager getDatastorePropertyManager() {
        return new JsonFileDatastorePropertyManager();
    }
}
