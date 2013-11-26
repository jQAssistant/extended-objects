package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.CollectionPropertyMethodMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.Collection;

public class CollectionPropertySetMethod extends AbstractPropertyMethod<CollectionPropertyMethodMetadata> {

    private RelationshipManager relationshipManager;

    public CollectionPropertySetMethod(CollectionPropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
        relationshipManager = new RelationshipManager(metadata);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        Object value = args[0];
        relationshipManager.removeRelationships(node);
        Collection<?> collection = (Collection<?>) value;
        for (Object o : collection) {
            Node endNode = getInstanceManager().getNode(o);
            relationshipManager.createRelationship(node, endNode);
        }
        return null;
    }
}
