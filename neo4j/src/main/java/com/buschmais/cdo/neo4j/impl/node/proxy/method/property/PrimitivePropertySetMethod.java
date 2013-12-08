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
        if (value != null) {
            if (Enum.class.isAssignableFrom(getMetadata().getBeanMethod().getType())) {
                value = ((Enum) value).name();
            }
            getPropertyManager().setProperty(entity, getMetadata(), value);
        } else {
            if (getPropertyManager().hasProperty(entity, getMetadata())) {
                getPropertyManager().removeProperty(entity, getMetadata());
            }
        }
        return null;
    }
}
