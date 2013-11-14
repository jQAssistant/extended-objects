package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.metadata.ReferencePropertyMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertyGetMethod extends AbstractPropertyMethod<ReferencePropertyMetadata> {

    public ReferencePropertyGetMethod(ReferencePropertyMetadata metadata, InstanceManager instanceManager) {
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
