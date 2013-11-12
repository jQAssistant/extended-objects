package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class ToStringMethod implements ProxyMethod {

    private InstanceManager instanceManager;

    public ToStringMethod(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(Node node, Object[] args) {
        Class<Object> type = instanceManager.getType(node);
        StringBuffer stringBuffer = new StringBuffer(type.getName());
        stringBuffer.append(", id=");
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
