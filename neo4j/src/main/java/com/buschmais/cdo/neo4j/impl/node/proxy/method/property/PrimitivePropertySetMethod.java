package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import org.neo4j.graphdb.Node;

public class PrimitivePropertySetMethod extends AbstractPropertyMethod<PrimitivePropertyMethodMetadata> {

    public PrimitivePropertySetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node entity, Object instance, Object[] args) {
        Object value = args[0];
        String propertyName = getMetadata().getPropertyName();
        if (value != null) {
            if (Enum.class.isAssignableFrom(getMetadata().getBeanMethod().getType())) {
                value = ((Enum) value).name();
            }
            entity.setProperty(propertyName, value);
        } else {
            if (entity.hasProperty(propertyName)) {
                entity.removeProperty(propertyName);
            }
        }
        return null;
    }
}
