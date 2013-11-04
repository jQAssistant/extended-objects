package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.metadata.CollectionPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.collection.SetProxy;
import org.neo4j.graphdb.Node;

import java.util.Set;

public class CollectionPropertyGetMethod extends AbstractPropertyMethod<CollectionPropertyMetadata> {

    public CollectionPropertyGetMethod(CollectionPropertyMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    @Override
    public Object invoke(Node node, Object[] args) {
        CollectionPropertyMetadata collectionPropertyMetadata = getMetadata();
        if (Set.class.isAssignableFrom(collectionPropertyMetadata.getBeanProperty().getType())) {
            return new SetProxy(node, collectionPropertyMetadata.getRelationshipType(), getInstanceManager());
        }
        throw new CdoManagerException("Unsupported collection type " + collectionPropertyMetadata.getBeanProperty().getType());
    }
}
