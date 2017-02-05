package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.util.Map;

import org.neo4j.graphdb.DynamicRelationshipType;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.embedded.impl.datastore.metadata.*;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedLabel;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedRelationshipType;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.metadata.method.IndexedPropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedElement;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;
import com.buschmais.xo.spi.reflection.AnnotatedType;
import com.buschmais.xo.spi.reflection.PropertyMethod;
import com.google.common.base.CaseFormat;

/**
 * {@link com.buschmais.xo.spi.datastore.DatastoreMetadataFactory}
 * implementation for Neo4j datastores.
 */
public class Neo4jMetadataFactory implements DatastoreMetadataFactory<NodeMetadata, EmbeddedLabel, RelationshipMetadata, EmbeddedRelationshipType> {

    @Override
    public NodeMetadata createEntityMetadata(AnnotatedType annotatedType, Map<Class<?>, TypeMetadata> metadataByType) {
        Label labelAnnotation = annotatedType.getAnnotation(Label.class);
        EmbeddedLabel label = null;
        IndexedPropertyMethodMetadata<IndexedPropertyMetadata> indexedProperty = null;
        if (labelAnnotation != null) {
            String value = labelAnnotation.value();
            if (Label.DEFAULT_VALUE.equals(value)) {
                value = annotatedType.getName();
            }
            label = new EmbeddedLabel(value);
            Class<?> usingIndexOf = labelAnnotation.usingIndexedPropertyOf();
            if (!Object.class.equals(usingIndexOf)) {
                TypeMetadata typeMetadata = metadataByType.get(usingIndexOf);
                indexedProperty = typeMetadata.getIndexedProperty();
            }
        }
        return new NodeMetadata(label, indexedProperty);
    }

    @Override
    public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(AnnotatedMethod annotatedMethod) {
        return null;
    }

    @Override
    public <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod propertyMethod) {
        return null;
    }

    @Override
    public PropertyMetadata createPropertyMetadata(PropertyMethod propertyMethod) {
        Property property = propertyMethod.getAnnotationOfProperty(Property.class);
        String name = property != null ? property.value() : propertyMethod.getName();
        return new PropertyMetadata(name);
    }

    @Override
    public IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod propertyMethod) {
        Indexed indexed = propertyMethod.getAnnotation(Indexed.class);
        return new IndexedPropertyMetadata(indexed.create(), indexed.unique());
    }

    @Override
    public RelationshipMetadata createRelationMetadata(AnnotatedElement<?> annotatedElement, Map<Class<?>, TypeMetadata> metadataByType) {
        Relation relationAnnotation;
        if (annotatedElement instanceof PropertyMethod) {
            relationAnnotation = ((PropertyMethod) annotatedElement).getAnnotationOfProperty(Relation.class);
        } else {
            relationAnnotation = annotatedElement.getAnnotation(Relation.class);
        }
        String name = null;
        if (relationAnnotation != null) {
            String value = relationAnnotation.value();
            if (!Relation.DEFAULT_VALUE.equals(value)) {
                name = value;
            }
        }
        if (name == null) {
            name = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, annotatedElement.getName());
        }
        return new RelationshipMetadata(new EmbeddedRelationshipType(DynamicRelationshipType.withName(name)));
    }
}
