package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.Node;

public class PrimitivePropertySetMethod extends AbstractPropertyMethod<PrimitivePropertyMetadata> {

    public PrimitivePropertySetMethod(PrimitivePropertyMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object[] args) {
        Object value = args[0];
        String propertyName = getMetadata().getPropertyName();
        if (value != null) {
            node.setProperty(propertyName, value);
        } else {
            if (node.hasProperty(propertyName)) {
                node.removeProperty(propertyName);
            }
        }
        return null;
    }
}
