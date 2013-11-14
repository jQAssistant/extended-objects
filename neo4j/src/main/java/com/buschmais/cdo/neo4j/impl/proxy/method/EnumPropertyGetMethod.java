package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.metadata.EnumMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;

public class EnumPropertyGetMethod extends AbstractPropertyMethod<EnumMethodMetadata> {

    public EnumPropertyGetMethod(EnumMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object instance, Object[] args) {
        for (Enum<?> enumerationValue : getMetadata().getEnumerationType().getEnumConstants()) {
            if (node.hasLabel(DynamicLabel.label(enumerationValue.name()))) {
                return enumerationValue;
            }
        }
        return null;
    }
}
