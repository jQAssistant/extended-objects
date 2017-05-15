package com.buschmais.xo.impl.proxy.common.object;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

public abstract class AbstractDatastoreTypeToStringMethod<T> extends AbstractToStringMethod<T> {

    @Override
    protected Map<String, Object> getProperties(T datastoreType) {
        SortedMap<String, Object> properties = new TreeMap<>();
        for (TypeMetadata type : getTypes(datastoreType)) {
            addProperties(datastoreType, type, properties);
        }
        return properties;
    }

    private void addProperties(T datastoreType, TypeMetadata type, SortedMap<String, Object> properties) {
        for (MethodMetadata<?, ?> methodMetadata : type.getProperties()) {
            if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                PrimitivePropertyMethodMetadata propertyMethodMetadata = (PrimitivePropertyMethodMetadata) methodMetadata;
                Object value = getProperty(datastoreType, propertyMethodMetadata);
                properties.put(propertyMethodMetadata.getAnnotatedMethod().getName(), value);
            }
        }
        for (TypeMetadata superType : type.getSuperTypes()) {
            addProperties(datastoreType, superType, properties);
        }
    }

    protected abstract TypeMetadataSet<?> getTypes(T datastoreType);

    protected abstract Object getProperty(T datastoreType, PrimitivePropertyMethodMetadata propertyMethodMetadata);

}
