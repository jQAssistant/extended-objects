package com.buschmais.cdo.neo4j.impl.proxy;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.impl.proxy.method.ProxyMethodService;
import org.neo4j.graphdb.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class NodeInvocationHandler implements InvocationHandler {

    private Node node;
    private ProxyMethodService proxyMethodService;

    public NodeInvocationHandler(Node node, ProxyMethodService proxyMethodService) {
        this.node = node;
        this.proxyMethodService = proxyMethodService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (node == null) {
            throw new CdoManagerException("Invalid access to an un-managed instance.");
        }
        return proxyMethodService.invoke(node, proxy, method, args);
    }

    public Node getNode() {
        return node;
    }

    public void close() {
        node = null;
    }
}
