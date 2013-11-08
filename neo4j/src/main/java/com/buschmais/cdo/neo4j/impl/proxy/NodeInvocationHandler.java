package com.buschmais.cdo.neo4j.impl.proxy;

import com.buschmais.cdo.api.CdoManagerException;
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
        if (node == null) {
            throw new CdoManagerException("Invalid access to an un-managed instance.");
        }
        return instanceManager.invoke(node, method, args);
    }

    public Node getNode() {
        return node;
    }

    public void close() {
        node = null;
    }
}
