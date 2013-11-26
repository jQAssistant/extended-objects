package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.CollectionPropertyMethodMetadata;
import org.neo4j.graphdb.Node;

import java.util.Collection;

public class CollectionPropertySetMethod extends AbstractPropertyMethod<CollectionPropertyMethodMetadata> {

    private RelationshipManager relationshipManager;

    public CollectionPropertySetMethod(CollectionPropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
        relationshipManager = new RelationshipManager(metadata);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        Object value = args[0];
        relationshipManager.removeRelationships(entity);
        Collection<?> collection = (Collection<?>) value;
        for (Object o : collection) {
            Node endNode = getInstanceManager().getEntity(o);
            relationshipManager.createRelationship(entity, endNode);
        }
        return null;
    }
}
