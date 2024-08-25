package com.buschmais.xo.impl.proxy.common.object;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.buschmais.xo.api.metadata.method.MethodMetadata;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.CompositeTypeMetadata;
import com.buschmais.xo.api.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;

public abstract class AbstractDatastoreTypeToStringMethod<T> extends AbstractToStringMethod<T> {

    @Override
    protected Map<String, Object> getProperties(T datastoreType) {
        SortedMap<String, Object> properties = new TreeMap<>();
        for (TypeMetadata type : getDynamicType(datastoreType).getMetadata()) {
            addProperties(datastoreType, type, properties);
        }
        return properties;
    }

    private void addProperties(T datastoreType, TypeMetadata type, SortedMap<String, Object> properties) {
        for (MethodMetadata<?, ?> methodMetadata : type.getProperties()) {
            if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                PrimitivePropertyMethodMetadata propertyMethodMetadata = (PrimitivePropertyMethodMetadata) methodMetadata;
                Object value = getProperty(datastoreType, propertyMethodMetadata);
                if (value != null) {
                    properties.put(propertyMethodMetadata.getAnnotatedMethod()
                        .getName(), value);
                }
            }
        }
        for (TypeMetadata superType : type.getSuperTypes()) {
            addProperties(datastoreType, superType, properties);
        }
    }

    private final Object getProperty(T datastoreType, PrimitivePropertyMethodMetadata propertyMethodMetadata) {
        DatastorePropertyManager<T, ?> datastorePropertyManager = getDatastorePropertyManager();
        return datastorePropertyManager.hasProperty(datastoreType, propertyMethodMetadata) ?
            datastorePropertyManager.getProperty(datastoreType, propertyMethodMetadata) :
            null;
    }

    protected abstract CompositeTypeMetadata<?> getDynamicType(T datastoreType);

    protected abstract DatastorePropertyManager<T, ?> getDatastorePropertyManager();
}
