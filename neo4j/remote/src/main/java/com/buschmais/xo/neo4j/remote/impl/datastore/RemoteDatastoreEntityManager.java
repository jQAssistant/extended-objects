package com.buschmais.xo.neo4j.remote.impl.datastore;

import static com.buschmais.xo.neo4j.spi.helper.MetadataHelper.getIndexedPropertyMetadata;
import static org.neo4j.driver.v1.Values.parameters;

import java.util.*;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.*;
import com.buschmais.xo.neo4j.remote.impl.model.state.NodeState;
import com.buschmais.xo.neo4j.remote.impl.model.state.RelationshipState;
import com.buschmais.xo.neo4j.remote.impl.model.state.StateTracker;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.AbstractRelationPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

public class RemoteDatastoreEntityManager extends AbstractRemoteDatastorePropertyManager<RemoteNode, NodeState>
        implements DatastoreEntityManager<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, PropertyMetadata> {

    private long idSequence = -1;

    public RemoteDatastoreEntityManager(StatementExecutor statementExecutor, RemoteDatastoreSessionCache datastoreSessionCache) {
        super(statementExecutor, datastoreSessionCache);
    }

    @Override
    public boolean isEntity(Object o) {
        return RemoteNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<RemoteLabel> getEntityDiscriminators(RemoteNode remoteNode) {
        ensureLoaded(remoteNode);
        return remoteNode.getState().getLabels().getElements();
    }

    @Override
    public Long getEntityId(RemoteNode remoteNode) {
        return remoteNode.getId();
    }

    @Override
    public RemoteNode createEntity(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> types, Set<RemoteLabel> remoteLabels,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        Map<String, Object> properties = getProperties(exampleEntity);
        NodeState nodeState = new NodeState(remoteLabels, properties);
        initializeEntity(types, nodeState);
        RemoteNode remoteNode;
        if (isBatchable(types)) {
            long id = idSequence--;
            remoteNode = datastoreSessionCache.getNode(id, nodeState);
        } else {
            StringBuilder labels = getLabelExpression(remoteLabels);
            Record record;
            if (properties.isEmpty()) {
                String statement = "CREATE (n" + labels.toString() + ") RETURN id(n) as id";
                record = statementExecutor.getSingleResult(statement, Collections.emptyMap());
            } else {
                String statement = "CREATE (n" + labels.toString() + "{n}) RETURN id(n) as id";
                record = statementExecutor.getSingleResult(statement, parameters("n", properties));
            }
            long id = record.get("id").asLong();
            remoteNode = datastoreSessionCache.getNode(id, nodeState);
        }
        return remoteNode;
    }

    /**
     * Determine if at least one type is marked as
     * {@link com.buschmais.xo.neo4j.api.annotation.Batchable}.
     *
     * @param types
     *            The types.
     * @return <code>true</code> if batching may be used.
     */
    private boolean isBatchable(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> types) {
        for (EntityTypeMetadata<NodeMetadata<RemoteLabel>> type : types) {
            if (type.getDatastoreMetadata().isBatchable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes all relation properties of the given node state with empty
     * collections.
     *
     * @param types
     *            The types.
     * @param nodeState
     *            The state of the entity.
     */
    private void initializeEntity(Collection<? extends TypeMetadata> types, NodeState nodeState) {
        for (TypeMetadata type : types) {
            Collection<TypeMetadata> superTypes = type.getSuperTypes();
            initializeEntity(superTypes, nodeState);
            for (MethodMetadata<?, ?> methodMetadata : type.getProperties()) {
                if (methodMetadata instanceof AbstractRelationPropertyMethodMetadata) {
                    AbstractRelationPropertyMethodMetadata<?> relationPropertyMethodMetadata = (AbstractRelationPropertyMethodMetadata) methodMetadata;
                    RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> relationshipMetadata = relationPropertyMethodMetadata
                            .getRelationshipMetadata();
                    RemoteRelationshipType relationshipType = relationshipMetadata.getDatastoreMetadata().getDiscriminator();
                    RelationTypeMetadata.Direction direction = relationPropertyMethodMetadata.getDirection();
                    RemoteDirection remoteDirection;
                    switch (direction) {
                    case FROM:
                        remoteDirection = RemoteDirection.OUTGOING;
                        break;
                    case TO:
                        remoteDirection = RemoteDirection.INCOMING;
                        break;
                    default:
                        throw new XOException("Unsupported direction: " + direction);
                    }
                    if (nodeState.getRelationships(remoteDirection, relationshipType) == null) {
                        nodeState.setRelationships(remoteDirection, relationshipType, new StateTracker<>(new LinkedHashSet<>()));
                    }
                }
            }
        }
    }

    @Override
    public void deleteEntity(RemoteNode remoteNode) {
        StatementBatchBuilder batchBuilder = new StatementBatchBuilder(statementExecutor);
        for (StateTracker<RemoteRelationship, Set<RemoteRelationship>> tracker : remoteNode.getState().getOutgoingRelationships().values()) {
            flushRemovedRelationships(batchBuilder, tracker.getRemoved());
        }
        String statement = "MATCH (n) WHERE id(n)=entry['n'] DELETE n RETURN collect(id(n))";
        batchBuilder.add(statement, parameters("n", remoteNode.getId()));
        batchBuilder.execute();
    }

    @Override
    public RemoteNode findEntityById(EntityTypeMetadata<NodeMetadata<RemoteLabel>> metadata, RemoteLabel remoteLabel, Long id) {
        Node node = fetch(id);
        return datastoreSessionCache.getNode(node);
    }

    @Override
    public ResultIterator<RemoteNode> findEntity(EntityTypeMetadata<NodeMetadata<RemoteLabel>> type, RemoteLabel remoteLabel,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> values) {
        if (values.size() > 1) {
            throw new XOException("Only one property value is supported for find operation");
        }
        Map.Entry<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entry = values.entrySet().iterator().next();
        PropertyMetadata propertyMetadata = getIndexedPropertyMetadata(type, entry.getKey());
        Object value = entry.getValue();
        String statement = String.format("MATCH (n:%s) WHERE n.%s={v} RETURN n", remoteLabel.getName(), propertyMetadata.getName());
        StatementResult result = statementExecutor.execute(statement, parameters("v", value));
        return new ResultIterator<RemoteNode>() {
            @Override
            public boolean hasNext() {
                return result.hasNext();
            }

            @Override
            public RemoteNode next() {
                Record record = result.next();
                Node node = record.get("n").asNode();
                return datastoreSessionCache.getNode(node);
            }

            @Override
            public void close() {
                result.consume();
            }
        };
    }

    @Override
    public void addDiscriminators(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> types, RemoteNode remoteNode, Set<RemoteLabel> remoteLabels) {
        NodeState state = remoteNode.getState();
        state.getLabels().addAll(remoteLabels);
        initializeEntity(types, state);
    }

    @Override
    public void removeDiscriminators(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> removedTypes, RemoteNode remoteNode,
            Set<RemoteLabel> remoteLabels) {
        remoteNode.getState().getLabels().removeAll(remoteLabels);
    }

    @Override
    protected Node load(RemoteNode remoteNode) {
        return fetch(remoteNode.getId());
    }

    private Node fetch(Long id) {
        Record record = statementExecutor.getSingleResult("MATCH (n) WHERE id(n)={id} RETURN n", parameters("id", id));
        return record.get("n").asNode();
    }

    /**
     * Creates an expression for adding labels, e.g. ":Person:Customer".
     *
     * @param remoteLabels
     *            The labels.
     * @return The expression.
     */
    private StringBuilder getLabelExpression(Set<RemoteLabel> remoteLabels) {
        StringBuilder labels = new StringBuilder();
        for (RemoteLabel remoteLabel : remoteLabels) {
            labels.append(':').append(remoteLabel.getName());
        }
        return labels;
    }

    @Override
    public void flush(Iterable<RemoteNode> entities) {
        StatementBatchBuilder batchBuilder = new StatementBatchBuilder(statementExecutor);
        for (RemoteNode entity : entities) {
            if (entity.getId() < 1) {
                flushAddedEntity(batchBuilder, entity);
            }
        }
        batchBuilder.execute();
        for (RemoteNode entity : entities) {
            flush(batchBuilder, entity, "(n)", "n");
            flushLabels(batchBuilder, entity);
            for (StateTracker<RemoteRelationship, Set<RemoteRelationship>> tracker : entity.getState().getOutgoingRelationships().values()) {
                flushAddedRelationships(batchBuilder, tracker.getAdded());
                flushRemovedRelationships(batchBuilder, tracker.getRemoved());
            }
            entity.getState().flush();
        }
        batchBuilder.execute();
    }

    private void flushAddedEntity(StatementBatchBuilder batchBuilder, RemoteNode entity) {
        Map<String, Object> properties = entity.getProperties();
        StringBuilder labelExpression = getLabelExpression(entity.getLabels());
        String statement = "CREATE (n" + labelExpression.toString() + ") SET n=entry['n'] RETURN collect({oldId:entry['id'], newId:id(n)}) as nodes";
        batchBuilder.add(statement, parameters("id", entity.getId(), "n", properties), result -> {
            List<Object> nodes = result.get("nodes").asList();
            for (Object node : nodes) {
                Map<String, Object> r = (Map<String, Object>) node;
                Long oldId = (Long) r.get("oldId");
                Long newId = (Long) r.get("newId");
                RemoteNode oldNode = datastoreSessionCache.getNode(oldId);
                NodeState state = oldNode.getState();
                RemoteNode newNode = datastoreSessionCache.getNode(newId, state);
            }
        });
    }

    private void flushLabels(StatementBatchBuilder batchBuilder, RemoteNode node) {
        StateTracker<RemoteLabel, Set<RemoteLabel>> labels = node.getState().getLabels();
        Set<RemoteLabel> added = labels.getAdded();
        if (!added.isEmpty()) {
            StringBuilder addedLabelsExpression = getLabelExpression(added);
            String statement = "MATCH (n) WHERE id(n)=entry['n'] SET n" + addedLabelsExpression + " RETURN collect(id(n))";
            batchBuilder.add(statement, parameters("n", node.getId()));
        }
        Set<RemoteLabel> removed = labels.getRemoved();
        if (!removed.isEmpty()) {
            StringBuilder removedLabelsExpression = getLabelExpression(removed);
            String statement = "MATCH (n) WHERE id(n)=entry['n'] REMOVE n" + removedLabelsExpression + " RETURN collect(id(n))";
            batchBuilder.add(statement, parameters("n", node.getId()));
        }
    }

    private void flushAddedRelationships(StatementBatchBuilder batchBuilder, Set<RemoteRelationship> addedRelationships) {
        for (RemoteRelationship addedRelationship : addedRelationships) {
            String statement = "MATCH (start),(end) WHERE id(start)=entry['start'] AND id(end)=entry['end'] CREATE (start)-[r:"
                    + addedRelationship.getType().getName() + "]->(end) SET r=entry['r'] RETURN collect({oldId:entry['id'], newId:id(r)}) as relations";
            batchBuilder.add(statement, parameters("start", addedRelationship.getStartNode().getId(), "id", addedRelationship.getId(), "r",
                    addedRelationship.getProperties(), "end", addedRelationship.getEndNode().getId()), result -> {
                        List<Object> relations = result.get("relations").asList();
                        for (Object relation : relations) {
                            Map<String, Object> r = (Map<String, Object>) relation;
                            Long oldId = (Long) r.get("oldId");
                            Long newId = (Long) r.get("newId");
                            RemoteRelationship oldRelationship = datastoreSessionCache.getRelationship(oldId);
                            RemoteNode startNode = oldRelationship.getStartNode();
                            RemoteNode endNode = oldRelationship.getEndNode();
                            RemoteRelationshipType type = oldRelationship.getType();
                            RelationshipState state = oldRelationship.getState();
                            RemoteRelationship newRelationship = datastoreSessionCache.getRelationship(newId, startNode, type, endNode, state);
                            replaceRelationship(startNode, oldRelationship, newRelationship, RemoteDirection.OUTGOING);
                            replaceRelationship(endNode, oldRelationship, newRelationship, RemoteDirection.INCOMING);
                        }
                    });
        }
    }

    private void replaceRelationship(RemoteNode node, RemoteRelationship oldRelationship, RemoteRelationship newRelationship, RemoteDirection direction) {
        StateTracker<RemoteRelationship, Set<RemoteRelationship>> relationships = node.getState().getRelationships(direction, oldRelationship.getType());
        if (relationships != null) {
            Set<RemoteRelationship> oldIncomingRelationships = relationships.getElements();
            oldIncomingRelationships.remove(oldRelationship);
            oldIncomingRelationships.add(newRelationship);
        }
    }

    private void flushRemovedRelationships(StatementBatchBuilder batchBuilder, Set<RemoteRelationship> removedRelationships) {
        for (RemoteRelationship removedRelationship : removedRelationships) {
            if (removedRelationship.getId() < 0) {
                String statement = "MATCH (start)-[r:" + removedRelationship.getType().getName()
                        + "]->(end) WHERE id(start)=entry['start'] AND id(end)=entry['end'] DELETE r RETURN collect(id(r))";
                batchBuilder.add(statement, parameters("start", removedRelationship.getStartNode().getId(), "end", removedRelationship.getEndNode().getId()));
            } else {
                String statement = "MATCH ()-[r]->() WHERE id(r)=entry['r'] DELETE r RETURN collect(id(r))";
                batchBuilder.add(statement, parameters("r", removedRelationship.getId()));
            }
        }
    }
}
