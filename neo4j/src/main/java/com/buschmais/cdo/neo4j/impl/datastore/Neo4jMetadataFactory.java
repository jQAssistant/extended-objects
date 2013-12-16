package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.api.annotation.Property;
import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.*;
import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.cdo.spi.metadata.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.RelationMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.reflection.TypeMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;

import java.util.Map;

public class Neo4jMetadataFactory implements DatastoreMetadataFactory<NodeMetadata, org.neo4j.graphdb.Label> {

    @Override
    public NodeMetadata createEntityMetadata(Class<?> type, Map<Class<?>, TypeMetadata<NodeMetadata>> metadataByType) {
        Label labelAnnotation = type.getAnnotation(Label.class);
        org.neo4j.graphdb.Label label = null;
        IndexedPropertyMethodMetadata<?> indexedProperty = null;
        if (labelAnnotation != null) {
            label = DynamicLabel.label(labelAnnotation.value());
            Class<?> usingIndexOf = labelAnnotation.usingIndexedPropertyOf();
            if (!Object.class.equals(usingIndexOf)) {
                indexedProperty = metadataByType.get(usingIndexOf).getIndexedProperty();
            }
        }
        return new NodeMetadata(label, indexedProperty);
    }

    @Override
    public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(TypeMethod typeMethod) {
        return null;
    }

    @Override
    public <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod beanPropertyMethod) {
        return null;
    }

    @Override
    public <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod beanPropertyMethod) {
        return null;
    }

    @Override
    public PrimitivePropertyMetadata createPrimitvePropertyMetadata(PropertyMethod beanPropertyMethod) {
        Property property = beanPropertyMethod.getPropertyAnnotation(Property.class);
        String name = property != null ? property.value() : beanPropertyMethod.getName();
        return new PrimitivePropertyMetadata(name);
    }

    @Override
    public EnumPropertyMetadata createEnumPropertyMetadata(PropertyMethod beanPropertyMethod) {
        Property property = beanPropertyMethod.getPropertyAnnotation(Property.class);
        String name = property != null ? property.value() : beanPropertyMethod.getName();
        return new EnumPropertyMetadata(name);
    }

    @Override
    public IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod beanMethod) {
        Indexed indexed = beanMethod.getAnnotation(Indexed.class);
        return new IndexedPropertyMetadata(indexed.create());
    }

    @Override
    public RelationshipMetadata createRelationMetadata(PropertyMethod beanPropertyMethod) {
        Relation relation = beanPropertyMethod.getPropertyAnnotation(Relation.class);
        String name = relation != null ? relation.value() : StringUtils.capitalize(beanPropertyMethod.getName());
        DynamicRelationshipType relationshipType = DynamicRelationshipType.withName(name);
        return new RelationshipMetadata(relationshipType);
    }

    public RelationMetadata.Direction getRelationDirection(PropertyMethod beanPropertyMethod) {
        Relation.Incoming incoming = beanPropertyMethod.getPropertyAnnotation(Relation.Incoming.class);
        Relation.Outgoing outgoing = beanPropertyMethod.getPropertyAnnotation(Relation.Outgoing.class);
        if (incoming != null && outgoing != null) {
            throw new CdoException("A relation property must be either incoming or outgoing: '" + beanPropertyMethod.getName() + "'");
        }
        if (incoming != null) {
            return RelationMetadata.Direction.INCOMING;
        }
        return RelationMetadata.Direction.OUTGOING;
    }
}
