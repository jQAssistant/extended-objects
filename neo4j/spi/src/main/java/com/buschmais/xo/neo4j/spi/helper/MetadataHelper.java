package com.buschmais.xo.neo4j.spi.helper;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

public class MetadataHelper {

    private MetadataHelper() {
    }

    public static <L extends Neo4jLabel> PropertyMetadata getIndexedPropertyMetadata(EntityTypeMetadata<NodeMetadata<L>> type,
            PrimitivePropertyMethodMetadata<PropertyMetadata> propertyMethodMetadata) {
        if (propertyMethodMetadata == null) {
            IndexedPropertyMethodMetadata<?> indexedProperty = type.getDatastoreMetadata().getUsingIndexedPropertyOf();
            if (indexedProperty == null) {
                throw new XOException("Type " + type.getAnnotatedType().getAnnotatedElement().getName() + " has no indexed property.");
            }
            propertyMethodMetadata = indexedProperty.getPropertyMethodMetadata();
        }
        return propertyMethodMetadata.getDatastoreMetadata();
    }

}
