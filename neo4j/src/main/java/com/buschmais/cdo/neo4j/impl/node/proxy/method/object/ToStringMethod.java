package com.buschmais.cdo.neo4j.impl.node.proxy.method.object;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class ToStringMethod implements NodeProxyMethod {

    private InstanceManager instanceManager;

    public ToStringMethod(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append('|');
            }
            stringBuffer.append(type);
        }
        stringBuffer.append(", id=");
        stringBuffer.append(Long.toString(entity.getId()));
        stringBuffer.append(" [");
        for (Label label : entity.getLabels()) {
            stringBuffer.append(label.name());
            stringBuffer.append(' ');
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
