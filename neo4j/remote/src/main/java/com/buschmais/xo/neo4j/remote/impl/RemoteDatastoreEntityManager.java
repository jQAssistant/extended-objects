package com.buschmais.xo.neo4j.remote.impl;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;
import org.neo4j.driver.v1.types.Node;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.remote.impl.model.state.NodeState;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

public class RemoteDatastoreEntityManager extends AbstractRemoteDatastorePropertyManager<RemoteNode, NodeState>
        implements DatastoreEntityManager<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, PropertyMetadata> {

    private RemoteDatastoreSessionCache datastoreCache;

    public RemoteDatastoreEntityManager(RemoteDatastoreTransaction transaction, RemoteDatastoreSessionCache datastoreCache) {
        super(transaction);
        this.datastoreCache = datastoreCache;
    }

    @Override
    public boolean isEntity(Object o) {
        return RemoteNode.class.isAssignableFrom(o.getClass());
    }

    @Override
    public Set<RemoteLabel> getEntityDiscriminators(RemoteNode remoteNode) {
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
        Map<String, Object> properties = new HashMap<>();
        for (Map.Entry<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entry : exampleEntity.entrySet()) {
            properties.put(entry.getKey().getDatastoreMetadata().getName(), entry.getValue());
        }
        String statement = String.format("CREATE (n%s{n}) RETURN id(n) as id", labels.toString());
        Record record = getSingleResult(statement, parameters("n", properties));
        long id = record.get("id").asLong();
        NodeState nodeState = new NodeState(remoteLabels, properties);
        return new RemoteNode(id, nodeState);
    }

    @Override
    public void deleteEntity(RemoteNode remoteNode) {
        String statement = "MATCH (n) WHERE id(n)=({id}) DELETE n RETURN count(n) as count";
        Record record = getSingleResult(statement, parameters("id", remoteNode.getId()));
        long count = record.get("count").asLong();
        if (count != 1) {
            throw new XOException("Could not delete " + remoteNode);
        }
    }

    @Override
    public RemoteNode findEntityById(EntityTypeMetadata<NodeMetadata<RemoteLabel>> metadata, RemoteLabel remoteLabel, Long id) {
        return datastoreCache.getNode(load(id));
    }

    @Override
    protected NodeState load(RemoteNode remoteNode) {
        return datastoreCache.getNodeState(load(remoteNode.getId()));
    }

    @Override
    public ResultIterator<RemoteNode> findEntity(EntityTypeMetadata<NodeMetadata<RemoteLabel>> type, RemoteLabel remoteLabel,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> values) {
        if (values.size() > 1) {
            throw new XOException("Only one property value is supported for find operation");
        }
        Map.Entry<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entry = values.entrySet().iterator().next();
        PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata = entry.getKey();
        if (propertyMethodMetadata == null) {
            IndexedPropertyMethodMetadata<?> indexedProperty = type.getDatastoreMetadata().getUsingIndexedPropertyOf();
            if (indexedProperty == null) {
                throw new XOException("Type " + type.getAnnotatedType().getAnnotatedElement().getName() + " has no indexed property.");
            }
            propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        }
        PropertyMetadata propertyMetadata = propertyMethodMetadata.getDatastoreMetadata();
        Object value = entry.getValue();
        String statement = String.format("MATCH (n:%s) WHERE n.%s={v} RETURN n", remoteLabel.getName(), propertyMetadata.getName());
        StatementResult result = transaction.getStatementRunner().run(statement, parameters("v", value));
        return new ResultIterator<RemoteNode>() {
            @Override
            public boolean hasNext() {
                return result.hasNext();
            }

            @Override
            public RemoteNode next() {
                Record record = result.next();
                Node node = record.get("n").asNode();
                return datastoreCache.getNode(node);
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
        Record record = getSingleResult(statement, parameters("id", remoteNode.getId()));
        long count = record.get("count").asLong();
        if (count != 1) {
            throw new XOException("Cannot add labels " + remoteLabels + " to node " + remoteNode);
        }
        remoteNode.getState().getLabels().addAll(remoteLabels);
    }

    @Override
    public void removeDiscriminators(RemoteNode remoteNode, Set<RemoteLabel> remoteLabels) {
        String statement = "MATCH (n) WHERE id(n)={id} REMOVE n" + getLabelExpression(remoteLabels) + " RETURN count(*) as count";
        Record record = getSingleResult(statement, parameters("id", remoteNode.getId()));
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

    private Node load(Long id) {
        Record record = getSingleResult("MATCH (n) WHERE id(n)={id} RETURN n", parameters("id", id));
        return record.get("n").asNode();
    }

    private Record getSingleResult(String statement, Value parameters) {
        StatementResult result = transaction.getStatementRunner().run(statement, parameters);
        try {
            return result.single();
        } catch (NoSuchRecordException e) {
            throw new XOException("Query returned no result.");
        } finally {
            result.consume();
        }
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

}
