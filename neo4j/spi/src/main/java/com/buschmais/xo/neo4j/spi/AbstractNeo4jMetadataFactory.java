package com.buschmais.xo.neo4j.spi;

import java.util.List;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.annotation.*;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.IndexedPropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
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
public abstract class AbstractNeo4jMetadataFactory<L extends Neo4jLabel, R extends Neo4jRelationshipType>
        implements DatastoreMetadataFactory<NodeMetadata<L>, L, RelationshipMetadata<R>, R> {

    @Override
    public NodeMetadata createEntityMetadata(AnnotatedType annotatedType, List<TypeMetadata> superTypes, Map<Class<?>, TypeMetadata> metadataByType) {
        Label labelAnnotation = annotatedType.getAnnotation(Label.class);
        L label = null;
        IndexedPropertyMethodMetadata<IndexedPropertyMetadata> indexedProperty = null;
        if (labelAnnotation != null) {
            String value = labelAnnotation.value();
            if (Label.DEFAULT_VALUE.equals(value)) {
                value = annotatedType.getName();
            }
            label = createLabel(value);
            Class<?> usingIndexOf = labelAnnotation.usingIndexedPropertyOf();
            if (!Object.class.equals(usingIndexOf)) {
                TypeMetadata typeMetadata = metadataByType.get(usingIndexOf);
                indexedProperty = typeMetadata.getIndexedProperty();
            }
        }
        boolean batchable = isBatchable(annotatedType);
        return new NodeMetadata<L>(label, indexedProperty, batchable);
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
    public RelationshipMetadata<R> createRelationMetadata(AnnotatedElement<?> annotatedElement, Map<Class<?>, TypeMetadata> metadataByType) {
        Relation relationAnnotation;
        boolean batchable;
        if (annotatedElement instanceof PropertyMethod) {
            relationAnnotation = ((PropertyMethod) annotatedElement).getAnnotationOfProperty(Relation.class);
            batchable = true;
        } else if (annotatedElement instanceof AnnotatedType) {
            AnnotatedType annotatedType = (AnnotatedType) annotatedElement;
            relationAnnotation = annotatedType.getAnnotation(Relation.class);
            batchable = annotatedType.getAnnotatedElement().isAnnotation() || isBatchable(annotatedElement);
        } else {
            throw new XOException("Annotated element is not supported: " + annotatedElement);
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
        return new RelationshipMetadata<R>(createRelationshipType(name), batchable);
    }

    private boolean isBatchable(AnnotatedElement<?> annotatedElement) {
        Batchable batchable = annotatedElement.getAnnotation(Batchable.class);
        return batchable != null ? batchable.value() : false;
    }

    protected abstract R createRelationshipType(String name);

    protected abstract L createLabel(String name);

}
