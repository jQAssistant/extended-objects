package com.buschmais.cdo.neo4j.impl.proxy.method;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class ToStringMethod implements ProxyMethod {

    @Override
    public Object invoke(Node node, Object[] args) {
        StringBuffer stringBuffer = new StringBuffer("Node ");
        stringBuffer.append(Long.toString(node.getId()));
        stringBuffer.append(" [");
        for (Label label : node.getLabels()) {
            stringBuffer.append(label.name());
            stringBuffer.append(' ');
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
