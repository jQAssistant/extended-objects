package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;

import java.util.*;

public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements DatastoreSession<Long, Node> {

    private GDS graphDatabaseService;
    private NodeMetadataProvider metadataProvider;

    public AbstractNeo4jDatastoreSession(GDS graphDatabaseService, NodeMetadataProvider metadataProvider) {
        this.graphDatabaseService = graphDatabaseService;
        this.metadataProvider = metadataProvider;
    }

    public GDS getGraphDatabaseService() {
        return graphDatabaseService;
    }

    @Override
    public Node create(List<Class<?>> types) {
        Node node = getGraphDatabaseService().createNode();
        Set<Label> labels = new HashSet<>();
        for (Class<?> currentType : types) {
            labels.addAll(metadataProvider.getNodeMetadata(currentType).getAggregatedLabels());
        }
        for (Label label : labels) {
            node.addLabel(label);
        }
        return node;
    }

    @Override
    public Iterator<Node> find(Class<?> type, Object value) {
        NodeMetadata nodeMetadata = metadataProvider.getNodeMetadata(type);
        Label label = nodeMetadata.getLabel();
        if (label == null) {
            throw new CdoException("Type " + type.getName() + " has no label.");
        }
        PrimitivePropertyMethodMetadata indexedProperty = nodeMetadata.getIndexedProperty();
        if (indexedProperty == null) {
            throw new CdoException("Type " + nodeMetadata.getType().getName() + " has no indexed property.");
        }
        final ResourceIterable<Node> nodesByLabelAndProperty = getGraphDatabaseService().findNodesByLabelAndProperty(label, indexedProperty.getPropertyName(), value);
        return nodesByLabelAndProperty.iterator();
    }

    @Override
    public void migrate(Node entity, List<Class<?>> types, List<Class<?>> targetTypes) {
        Set<Label> labels = new HashSet<>();
        for (Class<?> type : types) {
            NodeMetadata nodeMetadata = metadataProvider.getNodeMetadata(type);
            labels.addAll(nodeMetadata.getAggregatedLabels());
        }
        Set<Label> targetLabels = new HashSet<>();
        for (Class<?> currentType : targetTypes) {
            NodeMetadata targetMetadata = metadataProvider.getNodeMetadata(currentType);
            targetLabels.addAll(targetMetadata.getAggregatedLabels());
        }
        Set<Label> labelsToRemove = new HashSet<>(labels);
        labelsToRemove.removeAll(targetLabels);
        for (Label label : labelsToRemove) {
            entity.removeLabel(label);
        }
        Set<Label> labelsToAdd = new HashSet<>(targetLabels);
        labelsToAdd.removeAll(labels);
        for (Label label : labelsToAdd) {
            entity.addLabel(label);
        }
    }

    @Override
    public List<Class<?>> getTypes(Node entity) {
        // Collect all labels from the node
        Set<Label> labels = new HashSet<>();
        for (Label label : entity.getLabels()) {
            labels.add(label);
        }
        // Get all types matching the labels
        Set<Class<?>> types = new HashSet<>();
        for (Label label : labels) {
            Set<NodeMetadata> nodeMetadataOfLabel = metadataProvider.getNodeMetadata(label);
            if (nodeMetadataOfLabel != null) {
                for (NodeMetadata nodeMetadata : nodeMetadataOfLabel) {
                    if (labels.containsAll(nodeMetadata.getAggregatedLabels())) {
                        types.add(nodeMetadata.getType());
                    }
                }
            }
        }
        SortedSet<Class<?>> uniqueTypes = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
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
        return new ArrayList<>(uniqueTypes);
    }

    @Override
    public Long getId(Node entity) {
        return Long.valueOf(entity.getId());
    }
}