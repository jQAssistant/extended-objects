package com.buschmais.cdo.neo4j.impl.proxy.method.object;

import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

public class EqualsMethod implements ProxyMethod {

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
