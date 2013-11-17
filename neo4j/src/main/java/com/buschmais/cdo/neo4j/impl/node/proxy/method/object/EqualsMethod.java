package com.buschmais.cdo.neo4j.impl.node.proxy.method.object;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import org.neo4j.graphdb.Node;

public class EqualsMethod implements NodeProxyMethod {

    private InstanceManager instanceManager;

    public EqualsMethod(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        Object other = args[0];
        if (instanceManager.isNode(other)) {
            Node otherNode = instanceManager.getNode(other);
            boolean equal = (otherNode.getId() == node.getId());
            return Boolean.valueOf(equal);
        }
        return Boolean.valueOf(false);
    }
}
