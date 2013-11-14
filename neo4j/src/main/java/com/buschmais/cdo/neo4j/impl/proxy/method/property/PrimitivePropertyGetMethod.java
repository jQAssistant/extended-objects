package com.buschmais.cdo.neo4j.impl.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.Node;

public class PrimitivePropertyGetMethod extends AbstractPropertyMethod<PrimitivePropertyMethodMetadata> {

    public PrimitivePropertyGetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        String propertyName = getMetadata().getPropertyName();
        if (!node.hasProperty(propertyName)) {
            return null;
        }
        Object value = node.getProperty(propertyName);
        Class<?> type = getMetadata().getBeanMethod().getType();
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        return value;
    }
}
