package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertyGetMethod extends AbstractPropertyMethod<ReferencePropertyMethodMetadata> {

    private RelationshipManager relationshipManager;

    public ReferencePropertyGetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
        relationshipManager = new RelationshipManager(metadata);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        Node endNode = relationshipManager.getSingleRelationship(entity);
        return endNode != null ? getInstanceManager().getInstance(endNode) : null;
    }
}
