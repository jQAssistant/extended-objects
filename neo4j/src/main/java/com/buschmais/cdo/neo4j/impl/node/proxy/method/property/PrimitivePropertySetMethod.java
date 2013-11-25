package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import org.neo4j.graphdb.Node;

public class PrimitivePropertySetMethod extends AbstractPropertyMethod<PrimitivePropertyMethodMetadata> {

    public PrimitivePropertySetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        Object value = args[0];
        String propertyName = getMetadata().getPropertyName();
        if (value != null) {
            if (Enum.class.isAssignableFrom(getMetadata().getBeanMethod().getType())) {
                value = ((Enum) value).name();
            }
            node.setProperty(propertyName, value);
        } else {
            if (node.hasProperty(propertyName)) {
                node.removeProperty(propertyName);
            }
        }
        return null;
    }
}
