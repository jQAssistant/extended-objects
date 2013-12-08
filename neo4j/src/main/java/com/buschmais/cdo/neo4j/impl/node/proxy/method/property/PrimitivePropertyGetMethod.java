package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;

public class PrimitivePropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity,
        PrimitivePropertyMethodMetadata> {

    public PrimitivePropertyGetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        if (!getPropertyManager().hasProperty(entity, metadata)) {
            return null;
        }

        Object value = getPropertyManager().getProperty(entity, metadata);
        Class<?> type = metadata.getBeanMethod().getType();
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        return value;
    }
}
