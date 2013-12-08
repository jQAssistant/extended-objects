package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.CollectionPropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;

import java.util.Collection;

public class CollectionPropertySetMethod extends AbstractPropertyMethod<CollectionPropertyMethodMetadata> {

    public CollectionPropertySetMethod(CollectionPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        Object value = args[0];
        getPropertyManager().removeRelations(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection());
        Collection<?> collection = (Collection<?>) value;
        for (Object o : collection) {
            Node endNode = getInstanceManager().getEntity(o);
            getPropertyManager().createRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), endNode);
        }
        return null;
    }
}
