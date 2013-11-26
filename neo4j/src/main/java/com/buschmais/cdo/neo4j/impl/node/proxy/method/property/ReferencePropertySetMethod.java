package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReferencePropertySetMethod extends AbstractPropertyMethod<ReferencePropertyMethodMetadata> {

    private RelationshipManager relationshipManager;

    public ReferencePropertySetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
        relationshipManager = new RelationshipManager(metadata);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        Object value = args[0];
        Node target = value != null ? getInstanceManager().getNode(value) : null;
        relationshipManager.createSingleRelationship(node, target);
        return null;
    }
}
