package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import org.neo4j.graphdb.Node;

public class PrimitivePropertyGetMethod extends AbstractPropertyMethod<PrimitivePropertyMethodMetadata> {

    public PrimitivePropertyGetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        String propertyName = getMetadata().getPropertyName();
        if (!entity.hasProperty(propertyName)) {
            return null;
        }
        Object value = entity.getProperty(propertyName);
        Class<?> type = getMetadata().getBeanMethod().getType();
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        return value;
    }
}
