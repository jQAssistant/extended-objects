package com.buschmais.xo.neo4j.embedded.impl.converter;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.spi.helper.TypeConverter;

public class EmbeddedValueConverter implements TypeConverter {

    @Override
    public Class<?> getType() {
        return PropertyContainer.class;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof Node) {
            return new EmbeddedNode((Node) value);
        } else if (value instanceof Relationship) {
            return new EmbeddedRelationship((Relationship) value);
        }
        throw new XOException("Unsupported value type " + value);
    }
}
