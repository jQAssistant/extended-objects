package com.buschmais.xo.neo4j.embedded.impl.converter;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastoreTransaction;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNode;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationship;
import com.buschmais.xo.neo4j.spi.helper.TypeConverter;

import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class EmbeddedValueConverter implements TypeConverter {

    private final EmbeddedDatastoreTransaction transaction;

    public EmbeddedValueConverter(EmbeddedDatastoreTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Class<?> getType() {
        return Entity.class;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof Node) {
            return new EmbeddedNode(transaction, (Node) value);
        } else if (value instanceof Relationship) {
            return new EmbeddedRelationship(transaction, (Relationship) value);
        }
        throw new XOException("Unsupported value type " + value);
    }
}
