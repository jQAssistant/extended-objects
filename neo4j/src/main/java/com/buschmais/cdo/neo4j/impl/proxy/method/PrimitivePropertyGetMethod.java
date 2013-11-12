package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.Node;

public class PrimitivePropertyGetMethod extends AbstractPropertyMethod<PrimitivePropertyMetadata> {

    public PrimitivePropertyGetMethod(PrimitivePropertyMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object[] args) {
        String propertyName = getMetadata().getPropertyName();
        if (!node.hasProperty(propertyName)) {
            return null;
        }
        Object value = node.getProperty(propertyName);
        Class<?> type = getMetadata().getBeanProperty().getType();
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        return value;
    }
}
