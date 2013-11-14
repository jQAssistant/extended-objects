package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.metadata.ReferenceMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertyGetMethod extends AbstractPropertyMethod<ReferenceMethodMetadata> {

    public ReferencePropertyGetMethod(ReferenceMethodMetadata metadata, InstanceManager instanceManager) {
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
