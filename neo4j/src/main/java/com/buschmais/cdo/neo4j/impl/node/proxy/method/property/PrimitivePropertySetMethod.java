package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.common.PropertyManager;
import org.neo4j.graphdb.Node;

public class PrimitivePropertySetMethod<Entity> extends AbstractPropertyMethod<Entity, PrimitivePropertyMethodMetadata> {

    public PrimitivePropertySetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        if (value != null) {
            if (Enum.class.isAssignableFrom(metadata.getBeanMethod().getType())) {
                value = ((Enum) value).name();
            }
            getPropertyManager().setProperty(entity, metadata, value);
        } else {
            if (getPropertyManager().hasProperty(entity, metadata)) {
                getPropertyManager().removeProperty(entity, metadata);
            }
        }
        return null;
    }
}
