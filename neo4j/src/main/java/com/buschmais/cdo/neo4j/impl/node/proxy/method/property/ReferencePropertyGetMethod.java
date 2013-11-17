package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertyGetMethod extends AbstractPropertyMethod<ReferencePropertyMethodMetadata> {

    public ReferencePropertyGetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        RelationshipType relationshipType = getMetadata().getRelationshipType();
        Relationship singleRelationship = node.getSingleRelationship(relationshipType, Direction.OUTGOING);
        if (singleRelationship == null) {
            return null;
        }
        Node endNode = singleRelationship.getEndNode();
        return getInstanceManager().getInstance(endNode);
    }
}
