package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.metadata.CollectionPropertyMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.Collection;

public class CollectionPropertySetMethod extends AbstractPropertyMethod<CollectionPropertyMetadata> {

    public CollectionPropertySetMethod(CollectionPropertyMetadata metadata, InstanceManager instanceManager) {
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
