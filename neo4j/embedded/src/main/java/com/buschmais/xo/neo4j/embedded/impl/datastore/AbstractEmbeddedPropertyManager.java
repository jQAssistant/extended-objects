package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.util.Map;

import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.neo4j.embedded.impl.model.AbstractEmbeddedPropertyContainer;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;

/**
 * Abstract implementation of a {@link DatastorePropertyManager} for Neo4j.
 */
public abstract class AbstractEmbeddedPropertyManager<Element extends AbstractEmbeddedPropertyContainer>
    implements DatastorePropertyManager<Element, PropertyMetadata> {

    @Override
    public void setProperty(Element element, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        element.setProperty(metadata.getDatastoreMetadata()
            .getName(), value);
    }

    @Override
    public boolean hasProperty(Element element, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return element.hasProperty(metadata.getDatastoreMetadata()
            .getName());
    }

    @Override
    public void removeProperty(Element element, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        element.removeProperty(metadata.getDatastoreMetadata()
            .getName());
    }

    @Override
    public Object getProperty(Element element, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return element.getProperty(metadata.getDatastoreMetadata()
            .getName());
    }

    protected void setProperties(Element element, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        for (Map.Entry<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> entry : example.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                setProperty(element, entry.getKey(), value);
            }
        }
    }
}
