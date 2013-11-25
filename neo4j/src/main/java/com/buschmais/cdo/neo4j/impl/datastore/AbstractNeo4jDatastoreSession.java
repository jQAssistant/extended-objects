package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.common.AbstractIterableResult;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.spi.Datastore;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import org.neo4j.graphdb.*;

import java.util.*;

public abstract class AbstractNeo4jDatastoreSession<GDS extends GraphDatabaseService> implements DatastoreSession<Node> {

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
}
