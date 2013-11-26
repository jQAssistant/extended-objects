package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ReferencePropertyMethodMetadata;
import org.neo4j.graphdb.Node;

public class ReferencePropertySetMethod extends AbstractPropertyMethod<ReferencePropertyMethodMetadata> {

    private RelationshipManager relationshipManager;

    public ReferencePropertySetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
        relationshipManager = new RelationshipManager(metadata);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        Object value = args[0];
        Node target = value != null ? getInstanceManager().getEntity(value) : null;
        relationshipManager.createSingleRelationship(entity, target);
        return null;
    }
}
