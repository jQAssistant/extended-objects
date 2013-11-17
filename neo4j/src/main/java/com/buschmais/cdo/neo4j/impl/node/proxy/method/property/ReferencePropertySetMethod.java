package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertySetMethod extends AbstractPropertyMethod<ReferencePropertyMethodMetadata> {

    public ReferencePropertySetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        Object value = args[0];
        RelationshipType relationshipType = getMetadata().getRelationshipType();
        if (node.hasRelationship(relationshipType, Direction.OUTGOING)) {
            Relationship relationship = node.getSingleRelationship(relationshipType, Direction.OUTGOING);
            relationship.delete();
        }
        if (value != null) {
            Node endNode = getInstanceManager().getNode(value);
            node.createRelationshipTo(endNode, relationshipType);
        }
        return null;
    }
}
