package com.buschmais.cdo.neo4j.impl.proxy.method.object;

import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.List;

public class ToStringMethod implements ProxyMethod {

    private InstanceManager instanceManager;

    public ToStringMethod(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        List<Class<?>> types = instanceManager.getTypes(node);
        for (Class<?> type : types) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append('|');
            }
            stringBuffer.append(type);
        }
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
