package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.impl.converter.ValueConverter;

public abstract class AbstractPrimitivePropertyGetMethod<Entity, Relation, DatastoreType, PropertyManager extends AbstractPropertyManager<Entity, Relation, DatastoreType>>
    extends AbstractPropertyMethod<Entity, Relation, DatastoreType, PropertyManager, PrimitivePropertyMethodMetadata> {

    private final ValueConverter<Entity, Relation> valueConverter;

    protected AbstractPrimitivePropertyGetMethod(PropertyManager propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
        this.valueConverter = new ValueConverter<>(propertyManager.getSessionContext());
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        PropertyManager propertyManager = getPropertyManager();
        Class<?> propertyType = metadata.getAnnotatedMethod()
            .getType();
        return valueConverter.convert(propertyManager.hasProperty(datastoreType, metadata) ? propertyManager.getProperty(datastoreType, metadata) : null,
            propertyType);
    }

}
