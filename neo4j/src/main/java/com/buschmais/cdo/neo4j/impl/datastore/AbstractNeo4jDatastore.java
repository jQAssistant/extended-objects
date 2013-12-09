package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.api.annotation.Property;
import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.UserDefinedMethod;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.EnumPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.neo4j.spi.Datastore;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.DynamicRelationshipType;

import static com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction;

public abstract class AbstractNeo4jDatastore<DS extends AbstractNeo4jDatastoreSession> implements Datastore<DS> {

    @Override
    public <EntityMetadata> EntityMetadata createEntityMetadata(Class<?> type) {
        return null;
    }

    @Override
    public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(BeanMethod beanMethod) {
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
        Property property = beanPropertyMethod.getAnnotation(Property.class);
        String name = property != null ? property.value() : beanPropertyMethod.getName();
        return new PrimitivePropertyMetadata(name);
    }

    @Override
    public EnumPropertyMetadata createEnumPropertyMetadata(PropertyMethod beanPropertyMethod) {
        Property property = beanPropertyMethod.getAnnotation(Property.class);
        String name = property != null ? property.value() : beanPropertyMethod.getName();
        return new EnumPropertyMetadata(name);
    }

    @Override
    public <ImplementedByMetadata> ImplementedByMetadata createIndexedPropertyMetadata(PropertyMethod beanMethod) {
        return null;
    }

    @Override
    public RelationshipMetadata createRelationMetadata(PropertyMethod beanPropertyMethod) {
        Relation relation = beanPropertyMethod.getAnnotation(Relation.class);
        String name = relation != null ? relation.value() : StringUtils.capitalize(beanPropertyMethod.getName());
        DynamicRelationshipType relationshipType = DynamicRelationshipType.withName(name);
        return new RelationshipMetadata(relationshipType);
    }

    public Direction getRelationDirection(PropertyMethod beanPropertyMethod) {
        Relation.Incoming incoming = beanPropertyMethod.getAnnotation(Relation.Incoming.class);
        Relation.Outgoing outgoing = beanPropertyMethod.getAnnotation(Relation.Outgoing.class);
        if (incoming != null && outgoing != null) {
            throw new CdoException("A relation property must be either incoming or outgoing: '" + beanPropertyMethod.getName() + "'");
        }
        if (incoming != null) {
            return Direction.INCOMING;
        }
        return Direction.OUTGOING;
    }



}
