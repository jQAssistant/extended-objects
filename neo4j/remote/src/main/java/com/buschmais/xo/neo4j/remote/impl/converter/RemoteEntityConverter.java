package com.buschmais.xo.neo4j.remote.impl.converter;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.datastore.RemoteDatastoreSessionCache;
import com.buschmais.xo.neo4j.spi.helper.TypeConverter;

import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

public class RemoteEntityConverter implements TypeConverter {

    private RemoteDatastoreSessionCache sessionCache;

    public RemoteEntityConverter(RemoteDatastoreSessionCache sessionCache) {
        this.sessionCache = sessionCache;
    }

    @Override
    public Class<?> getType() {
        return Entity.class;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof Node) {
            return sessionCache.getNode((Node) value);
        } else if (value instanceof Relationship) {
            return sessionCache.getRelationship((Relationship) value);
        }
        throw new XOException("Unsupported value type " + value);
    }
}
