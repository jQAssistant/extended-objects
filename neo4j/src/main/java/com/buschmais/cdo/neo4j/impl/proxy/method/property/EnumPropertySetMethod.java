package com.buschmais.cdo.neo4j.impl.proxy.method.property;

import com.buschmais.cdo.neo4j.impl.metadata.EnumMethodMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class EnumPropertySetMethod extends AbstractPropertyMethod<EnumMethodMetadata> {

    public EnumPropertySetMethod(EnumMethodMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        Object value = args[0];
        for (Enum<?> enumerationValue : getMetadata().getEnumerationType().getEnumConstants()) {
            Label label = DynamicLabel.label(enumerationValue.name());
            if (enumerationValue.equals(value)) {
                node.addLabel(label);
            } else if (node.hasLabel(label)) {
                node.removeLabel(label);
            }
        }
        return null;
    }
}
