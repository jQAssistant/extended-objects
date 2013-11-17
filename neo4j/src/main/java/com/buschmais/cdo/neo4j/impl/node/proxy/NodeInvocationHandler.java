package com.buschmais.cdo.neo4j.impl.node.proxy;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.NodeProxyMethodService;
import org.neo4j.graphdb.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class NodeInvocationHandler implements InvocationHandler {

    private Node node;
    private NodeProxyMethodService nodeProxyMethodService;

    public NodeInvocationHandler(Node node, NodeProxyMethodService nodeProxyMethodService) {
        this.node = node;
        this.nodeProxyMethodService = nodeProxyMethodService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (node == null) {
            throw new CdoException("Invalid access to an un-managed instance.");
        }
        return nodeProxyMethodService.invoke(node, proxy, method, args);
    }

    public Node getNode() {
        return node;
    }

    public void close() {
        node = null;
    }
}
