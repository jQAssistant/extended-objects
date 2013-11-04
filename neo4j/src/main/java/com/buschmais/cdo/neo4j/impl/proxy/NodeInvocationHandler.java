package com.buschmais.cdo.neo4j.impl.proxy;

import org.neo4j.graphdb.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class NodeInvocationHandler implements InvocationHandler {

    private Node node;
    private InstanceManager instanceManager;

    public NodeInvocationHandler(Node node, InstanceManager instanceManager) {
        this.node = node;
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return instanceManager.invoke(node, method, args);
    }

    public Node getNode() {
        return node;
    }
}
