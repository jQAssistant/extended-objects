package com.buschmais.cdo.neo4j.impl.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
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
