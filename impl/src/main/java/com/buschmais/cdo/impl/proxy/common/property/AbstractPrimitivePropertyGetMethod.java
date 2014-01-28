package com.buschmais.cdo.impl.proxy.common.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public abstract class AbstractPrimitivePropertyGetMethod<DatastoreType, Entity, Relation> extends AbstractPropertyMethod<DatastoreType, Entity, Relation, PrimitivePropertyMethodMetadata> {

    public AbstractPrimitivePropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, PrimitivePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        AbstractPropertyManager<DatastoreType, Entity, Relation> propertyManager = getPropertyManager();
        Object value;
        if (!propertyManager.hasProperty(datastoreType, metadata)) {
            value = null;
        } else {
            value = propertyManager.getProperty(datastoreType, metadata);
        }
        return convert(value, metadata.getAnnotatedMethod().getType());
    }

    private Object convert(Object value, Class<?> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        } else if (value == null) {
            if (boolean.class.equals(type)) {
                return false;
            } else if (short.class.equals(type)) {
                return 0;
            } else if (int.class.equals(type)) {
                return 0;
            } else if (long.class.equals(type)) {
                return 0l;
            } else if (float.class.equals(type)) {
                return 0f;
            } else if (double.class.equals(type)) {
                return 0d;
            } else if (char.class.equals(type)) {
                return 0;
            } else if (byte.class.equals(type)) {
                return 0;
            }
        }
        return value;
    }
}
