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

public class RemoteDatastoreEntityManager extends AbstractRemoteDatastorePropertyManager<RemoteNode, NodeState>
        implements DatastoreEntityManager<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, PropertyMetadata> {

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
        return remoteNode.getState().getLabels();
    }

    @Override
    public Long getEntityId(RemoteNode remoteNode) {
        return remoteNode.getId();
    }

    @Override
    public RemoteNode createEntity(TypeMetadataSet<EntityTypeMetadata<NodeMetadata<RemoteLabel>>> types, Set<RemoteLabel> remoteLabels,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        StringBuilder labels = getLabelExpression(remoteLabels);
        Map<String, Object> properties = getProperties(exampleEntity);
        String statement = String.format("CREATE (n%s{n}) RETURN id(n) as id", labels.toString());
        Record record = statementExecutor.getSingleResult(statement, parameters("n", properties));
        long id = record.get("id").asLong();
        NodeState nodeState = new NodeState(remoteLabels, properties);
        for (EntityTypeMetadata<NodeMetadata<RemoteLabel>> type : types) {
            for (MethodMetadata<?, ?> methodMetadata : type.getProperties()) {
                if (methodMetadata instanceof AbstractRelationPropertyMethodMetadata) {
                    AbstractRelationPropertyMethodMetadata<?> relationPropertyMethodMetadata = (AbstractRelationPropertyMethodMetadata) methodMetadata;
                    RelationTypeMetadata<RelationshipMetadata<RemoteRelationshipType>> relationshipMetadata = relationPropertyMethodMetadata
                            .getRelationshipMetadata();
                    RemoteRelationshipType relationshipType = relationshipMetadata.getDatastoreMetadata().getDiscriminator();
                    RelationTypeMetadata.Direction direction = relationPropertyMethodMetadata.getDirection();
                    switch (direction) {
                    case FROM:
                        nodeState.setRelationships(RemoteDirection.OUTGOING, relationshipType, new StateTracker<>(new LinkedHashSet<>()));
                    case TO:
                        nodeState.setRelationships(RemoteDirection.INCOMING, relationshipType, new StateTracker<>(new LinkedHashSet<>()));
                    }
                }
            }
        }

        return datastoreSessionCache.getNode(id, nodeState);
    }

    @Override
    public void deleteEntity(RemoteNode remoteNode) {
        for (StateTracker<RemoteRelationship, Set<RemoteRelationship>> tracker : remoteNode.getState().getOutgoingRelationships().values()) {
            flushRemovedRelationships(tracker.getRemoved());
        }
        String statement = "MATCH (n) WHERE id(n)=({id}) DELETE n RETURN count(n) as count";
        Record record = statementExecutor.getSingleResult(statement, parameters("id", remoteNode.getId()));
        long count = record.get("count").asLong();
        if (count != 1) {
            throw new XOException("Could not delete " + remoteNode);
        }
    }

    @Override
    public RemoteNode findEntityById(EntityTypeMetadata<NodeMetadata<RemoteLabel>> metadata, RemoteLabel remoteLabel, Long id) {
        return datastoreSessionCache.getNode(id, datastoreSessionCache.getNodeState(fetch(id)));
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
                return datastoreSessionCache.getNode(node.id(), datastoreSessionCache.getNodeState(node));
            }

            @Override
            public void close() {
                result.consume();
            }
        };
    }

    @Override
    public void addDiscriminators(RemoteNode remoteNode, Set<RemoteLabel> remoteLabels) {
        String statement = "MATCH (n) WHERE id(n)={id} SET n" + getLabelExpression(remoteLabels) + " RETURN count(*) as count";
        Record record = statementExecutor.getSingleResult(statement, parameters("id", remoteNode.getId()));
        long count = record.get("count").asLong();
        if (count != 1) {
            throw new XOException("Cannot add labels " + remoteLabels + " to node " + remoteNode);
        }
        remoteNode.getState().getLabels().addAll(remoteLabels);
    }

    @Override
    public void removeDiscriminators(RemoteNode remoteNode, Set<RemoteLabel> remoteLabels) {
        String statement = "MATCH (n) WHERE id(n)={id} REMOVE n" + getLabelExpression(remoteLabels) + " RETURN count(*) as count";
        Record record = statementExecutor.getSingleResult(statement, parameters("id", remoteNode.getId()));
        long count = record.get("count").asLong();
        if (count != 1) {
            throw new XOException("Cannot remove labels " + remoteLabels + " from node " + remoteNode);
        }
        remoteNode.getState().getLabels().removeAll(remoteLabels);
    }

    protected String createIdentifier(int i) {
        return "n" + i;
    }

    protected String createIdentifierPattern(String nodeIdentifier) {
        return String.format("(%s)", nodeIdentifier);
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
        super.flush(entities);
        Set<RemoteRelationship> addedRelationships = new HashSet<>();
        Set<RemoteRelationship> removedRelationships = new HashSet<>();
        for (RemoteNode entity : entities) {
            for (StateTracker<RemoteRelationship, Set<RemoteRelationship>> tracker : entity.getState().getOutgoingRelationships().values()) {
                addedRelationships.addAll(tracker.getAdded());
                removedRelationships.addAll(tracker.getRemoved());
            }
        }
        flushRemovedRelationships(removedRelationships);
        flushAddedRelationships(addedRelationships);
        for (RemoteNode entity : entities) {
            entity.getState().clear();
        }
        super.flush(entities);
    }

    private void flushAddedRelationships(Set<RemoteRelationship> addedRelationships) {
        if (!addedRelationships.isEmpty()) {
            StringBuilder match = new StringBuilder();
            StringBuilder where = new StringBuilder();
            StringBuilder create = new StringBuilder();
            int i = 0;
            Map<String, Object> parameters = new HashMap<>();
            for (RemoteRelationship relationship : addedRelationships) {
                if (match.length() > 0) {
                    match.append(',');
                }
                if (where.length() > 0) {
                    where.append(" and ");
                }
                String startIdentifier = "s" + i;
                String endIdentifier = "e" + i;
                match.append(String.format("(%s),(%s)", startIdentifier, endIdentifier));
                where.append(String.format("id(%s)={%s} and id(%s)={%s}", startIdentifier, startIdentifier, endIdentifier, endIdentifier));
                create.append(" CREATE ").append(String.format("(%s)-[:%s]->(%s) ", startIdentifier, relationship.getType().getName(), endIdentifier));
                parameters.put(startIdentifier, relationship.getStartNode().getId());
                parameters.put(endIdentifier, relationship.getEndNode().getId());
                i++;
            }
            String statement = new StringBuilder().append("MATCH ").append(match).append(" WHERE ").append(where).append(create)
                    .append("RETURN count(*) as count").toString();
            Record result = statementExecutor.getSingleResult(statement, parameters);
            long count = result.get("count").asLong();
            if (count != 1) {
                throw new XOException("Cannot create relations.");
            }
        }
    }

    private void flushRemovedRelationships(Set<RemoteRelationship> removedRelationships) {
        if (!removedRelationships.isEmpty()) {
            StringBuilder match = new StringBuilder();
            StringBuilder where = new StringBuilder();
            StringBuilder delete = new StringBuilder();
            int i = 0;
            Map<String, Object> parameters = new HashMap<>();
            for (RemoteRelationship relationship : removedRelationships) {
                if (match.length() > 0) {
                    match.append(',');
                }
                if (where.length() > 0) {
                    where.append(" and ");
                }
                String identifier = "r" + i;
                match.append(String.format("()-[%s]->()", identifier));
                where.append(String.format("id(%s)={%s}", identifier, identifier));
                delete.append("DELETE ").append(identifier).append(' ');
                parameters.put(identifier, relationship.getId());
                i++;
            }
            String statement = new StringBuilder().append("MATCH ").append(match).append(" WHERE ").append(where).append(' ').append(delete)
                    .append("RETURN count(*) as count").toString();
            Record result = statementExecutor.getSingleResult(statement, parameters);
            long count = result.get("count").asLong();
            if (count != 1) {
                throw new XOException("Cannot remove relations.");
            }
        }
    }
}
