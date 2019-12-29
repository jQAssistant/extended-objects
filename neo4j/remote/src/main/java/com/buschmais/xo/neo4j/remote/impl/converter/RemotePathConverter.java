package com.buschmais.xo.neo4j.remote.impl.converter;

import java.util.ArrayList;
import java.util.List;

import com.buschmais.xo.neo4j.remote.impl.datastore.RemoteDatastoreSessionCache;
import com.buschmais.xo.neo4j.spi.helper.TypeConverter;

import org.neo4j.driver.types.Path;

/**
 * Converts a path into an iterable consisting of all nodes and relationships.
 */
public class RemotePathConverter implements TypeConverter {

    private RemoteDatastoreSessionCache sessionCache;

    public RemotePathConverter(RemoteDatastoreSessionCache sessionCache) {
        this.sessionCache = sessionCache;
    }

    @Override
    public Class<?> getType() {
        return Path.class;
    }

    @Override
    public Object convert(Object value) {
        Path path = (Path) value;
        List<Object> result = new ArrayList<>();
        for (Path.Segment segment : path) {
            if (result.isEmpty()) {
                result.add(sessionCache.getNode(segment.start()));
            }
            result.add(sessionCache.getRelationship(segment.relationship()));
            result.add(sessionCache.getNode(segment.end()));
        }
        return result;
    }
}
