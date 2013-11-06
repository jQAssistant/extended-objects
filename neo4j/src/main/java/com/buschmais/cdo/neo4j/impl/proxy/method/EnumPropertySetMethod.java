package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.neo4j.impl.metadata.EnumPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.Map;

public class EnumPropertySetMethod extends AbstractPropertyMethod<EnumPropertyMetadata> {

    public EnumPropertySetMethod(EnumPropertyMetadata metadata, InstanceManager instanceManager) {
        super(metadata, instanceManager);
    }

    public Object invoke(Node node, Object[] args) {
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
