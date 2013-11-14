package com.buschmais.cdo.neo4j.impl.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.metadata.CollectionMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.Collection;

public class CollectionPropertySetMethod extends AbstractPropertyMethod<CollectionMethodMetadata> {

    public CollectionPropertySetMethod(CollectionMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        Object value = args[0];
        Collection<?> collection = (Collection<?>) value;
        RelationshipType relationshipType = getMetadata().getRelationshipType();
        for (Relationship relationship : node.getRelationships(relationshipType, Direction.OUTGOING)) {
            relationship.delete();
        }
        for (Object o : collection) {
            Node endNode = getInstanceManager().getNode(o);
            node.createRelationshipTo(endNode, relationshipType);
        }
        return null;
    }
}
