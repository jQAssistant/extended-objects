package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.EnumPropertyMethodMetadata;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class EnumPropertySetMethod extends AbstractPropertyMethod<EnumPropertyMethodMetadata> {

    public EnumPropertySetMethod(EnumPropertyMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        Object value = args[0];
        for (Enum<?> enumerationValue : getMetadata().getEnumerationType().getEnumConstants()) {
            Label label = DynamicLabel.label(enumerationValue.name());
            if (enumerationValue.equals(value)) {
                entity.addLabel(label);
            } else if (entity.hasLabel(label)) {
                entity.removeLabel(label);
            }
        }
        return null;
    }
}
