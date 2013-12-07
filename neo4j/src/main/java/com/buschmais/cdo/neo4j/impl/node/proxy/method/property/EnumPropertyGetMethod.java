package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.EnumPropertyMethodMetadata;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;

public class EnumPropertyGetMethod extends AbstractPropertyMethod<EnumPropertyMethodMetadata> {

    public EnumPropertyGetMethod(EnumPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        for (Enum<?> enumerationValue : getMetadata().getEnumerationType().getEnumConstants()) {
            if (entity.hasLabel(DynamicLabel.label(enumerationValue.name()))) {
                return enumerationValue;
            }
        }
        return null;
    }
}
