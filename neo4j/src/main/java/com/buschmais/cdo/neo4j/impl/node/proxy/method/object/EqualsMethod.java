package com.buschmais.cdo.neo4j.impl.node.proxy.method.object;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import org.neo4j.graphdb.Node;

public class EqualsMethod implements NodeProxyMethod {

    private InstanceManager<Long,Node> instanceManager;

    public EqualsMethod(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        Object other = args[0];
        if (instanceManager.isEntity(other)) {
            Node otherNode = instanceManager.getEntity(other);
            boolean equal = (otherNode.getId() == entity.getId());
            return Boolean.valueOf(equal);
        }
        return Boolean.valueOf(false);
    }
}
