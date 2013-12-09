package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.api.annotation.Property;
import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.EnumPropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.PrimitivePropertyMetadata;
import com.buschmais.cdo.neo4j.impl.datastore.metadata.RelationshipMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.IndexedPropertyMethodMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.TypeMetadata;
import com.buschmais.cdo.neo4j.spi.Datastore;
import com.buschmais.cdo.neo4j.spi.DatastoreMetadataProvider;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;

import java.util.*;

import static com.buschmais.cdo.neo4j.impl.node.metadata.RelationMetadata.Direction;

public abstract class AbstractNeo4jDatastore<DS extends AbstractNeo4jDatastoreSession> implements Datastore<DS> {

    private Neo4jMetadataFactory metadataFactory = new Neo4jMetadataFactory();

    @Override
    public DatastoreMetadataProvider createMetadataProvider(Collection<TypeMetadata> entityTypes) {
        return new Neo4jMetadataProvider(entityTypes);
    }

    @Override
    public DatastoreMetadataFactory<?> getMetadataFactory() {
        return metadataFactory;
    }

    public class Neo4jMetadataFactory implements DatastoreMetadataFactory<NodeMetadata> {

        @Override
        public NodeMetadata createEntityMetadata(Class<?> type, Map<Class<?>, TypeMetadata> metadataByType) {
            Label labelAnnotation = type.getAnnotation(Label.class);
            SortedSet<org.neo4j.graphdb.Label> aggregatedLabels = new TreeSet<>(new Comparator<org.neo4j.graphdb.Label>() {
                @Override
                public int compare(org.neo4j.graphdb.Label o1, org.neo4j.graphdb.Label o2) {
                    return o1.name().compareTo(o2.name());
                }
            });
            org.neo4j.graphdb.Label label = null;
            IndexedPropertyMethodMetadata<?> indexedProperty = null;
            if (labelAnnotation != null) {
                label = DynamicLabel.label(labelAnnotation.value());
                aggregatedLabels.add(label);
                Class<?> usingIndexOf = labelAnnotation.usingIndexedPropertyOf();
                if (!Object.class.equals(usingIndexOf)) {
                    indexedProperty = metadataByType.get(usingIndexOf).getIndexedProperty();
                }
            }
            for (Class<?> implementedInterface : type.getInterfaces()) {
                TypeMetadata<NodeMetadata> superTypeMetadata = metadataByType.get(implementedInterface);
                aggregatedLabels.addAll(superTypeMetadata.getDatastoreMetadata().getAggregatedLabels());
            }
            return new NodeMetadata(label, aggregatedLabels, indexedProperty);
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
        public <ImplementedByMetadata> ImplementedByMetadata createIndexedPropertyMetadata(PropertyMethod beanMethod) {
            return null;
        }

        @Override
        public RelationshipMetadata createRelationMetadata(PropertyMethod beanPropertyMethod) {
            Relation relation = beanPropertyMethod.getPropertyAnnotation(Relation.class);
            String name = relation != null ? relation.value() : StringUtils.capitalize(beanPropertyMethod.getName());
            DynamicRelationshipType relationshipType = DynamicRelationshipType.withName(name);
            return new RelationshipMetadata(relationshipType);
        }

        public Direction getRelationDirection(PropertyMethod beanPropertyMethod) {
            Relation.Incoming incoming = beanPropertyMethod.getPropertyAnnotation(Relation.Incoming.class);
            Relation.Outgoing outgoing = beanPropertyMethod.getPropertyAnnotation(Relation.Outgoing.class);
            if (incoming != null && outgoing != null) {
                throw new CdoException("A relation property must be either incoming or outgoing: '" + beanPropertyMethod.getName() + "'");
            }
            if (incoming != null) {
                return Direction.INCOMING;
            }
            return Direction.OUTGOING;
        }
    }
}
