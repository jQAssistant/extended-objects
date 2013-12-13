package com.buschmais.cdo.store.json.impl;

import com.buschmais.cdo.spi.datastore.DatastoreMetadataProvider;
import com.buschmais.cdo.spi.datastore.TypeSet;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.store.json.impl.metadata.JsonNodeMetadata;
import org.codehaus.jackson.node.ObjectNode;

import java.util.*;

public class JsonFileDatastoreMetadataProvider implements DatastoreMetadataProvider<ObjectNode> {

    private Map<String, Set<TypeMetadata>> entityMetadataByName = new HashMap<>();

    public JsonFileDatastoreMetadataProvider(Collection<TypeMetadata> entityTypes) {
        for (TypeMetadata<JsonNodeMetadata> entityType : entityTypes) {
            // determine all possible metadata for a label
            Collection<String> aggregatedLabels = entityType.getDatastoreMetadata().getAggregatedTypeNames();
            for (String aggregatedLabel : aggregatedLabels) {
                Set<TypeMetadata> typeMetadataOfName = entityMetadataByName.get(aggregatedLabel);
                if (typeMetadataOfName == null) {
                    typeMetadataOfName = new HashSet<>();
                    entityMetadataByName.put(entityType.getClass().getName(), typeMetadataOfName);
                }
                typeMetadataOfName.add(entityType);
            }
        }
    }

    @Override
    public TypeSet getTypes(ObjectNode entity) {
        return null;
    }
}
