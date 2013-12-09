package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreMetadataProvider;
import com.buschmais.cdo.spi.datastore.TypeSet;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Neo4jMetadataProvider implements DatastoreMetadataProvider<Node> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jMetadataProvider.class);

    private Map<org.neo4j.graphdb.Label, Set<TypeMetadata>> entityMetadataByLabel = new HashMap<>();

    public Neo4jMetadataProvider(Collection<TypeMetadata> entityTypes) {
        for (TypeMetadata<NodeMetadata> entityType : entityTypes) {
            // determine all possible metadata for a label
            Set<Label> aggregatedLabels = entityType.getDatastoreMetadata().getAggregatedLabels();
            for (org.neo4j.graphdb.Label aggregatedLabel : aggregatedLabels) {
                Set<TypeMetadata> typeMetadataOfLabel = entityMetadataByLabel.get(aggregatedLabel);
                if (typeMetadataOfLabel == null) {
                    typeMetadataOfLabel = new HashSet<>();
                    entityMetadataByLabel.put(entityType.getDatastoreMetadata().getLabel(), typeMetadataOfLabel);
                }
                typeMetadataOfLabel.add(entityType);
            }
            LOGGER.info("Registering {}, labels={}.", entityType.getType().getName(), aggregatedLabels);
        }
    }

    @Override
    public TypeSet getTypes(Node entity) {
        // Collect all labels from the node
        Set<Label> labels = new HashSet<>();
        for (Label label : entity.getLabels()) {
            labels.add(label);
        }
        // Get all types matching the labels
        Set<Class<?>> types = new HashSet<>();
        for (Label label : labels) {
            Set<TypeMetadata> typeMetadataOfLabel = entityMetadataByLabel.get(label);
            if (typeMetadataOfLabel != null) {
                for (TypeMetadata<NodeMetadata> typeMetadata : typeMetadataOfLabel) {
                    if (labels.containsAll(typeMetadata.getDatastoreMetadata().getAggregatedLabels())) {
                        types.add(typeMetadata.getType());
                    }
                }
            }
        }
        TypeSet uniqueTypes = new TypeSet();
        // Remove super types if subtypes are already in the type set
        for (Class<?> type : types) {
            boolean subtype = false;
            for (Iterator<Class<?>> subTypeIterator = types.iterator(); subTypeIterator.hasNext() && !subtype; ) {
                Class<?> otherType = subTypeIterator.next();
                if (!type.equals(otherType) && type.isAssignableFrom(otherType)) {
                    subtype = true;
                }
            }
            if (!subtype) {
                uniqueTypes.add(type);
            }
        }
        return uniqueTypes;
    }
}
