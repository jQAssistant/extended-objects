package com.buschmais.cdo.neo4j.impl.metadata;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.api.annotation.Property;
import com.buschmais.cdo.neo4j.api.annotation.Relation;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class NodeMetadataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMetadata.class);
    private Map<Class<?>, NodeMetadata> nodeMetadataByType = new HashMap<>();
    private Map<org.neo4j.graphdb.Label, NodeMetadata> nodeMetadataByAggregatedLabels = new HashMap<>();

    public NodeMetadataProvider(Collection<Class<?>> types) {
        DependencyResolver.DependencyProvider<Class<?>> classDependencyProvider = new DependencyResolver.DependencyProvider<Class<?>>() {

            @Override
            public Set<Class<?>> getDependencies(Class<?> dependent) {
                return new HashSet<>(Arrays.asList(dependent.getInterfaces()));
            }
        };
        List<Class<?>> allTypes = DependencyResolver.newInstance(types, classDependencyProvider).resolve();
        LOGGER.debug("Processing types {}", allTypes);
        Map<Class<?>, Map<String, BeanProperty>> typeProperties = new HashMap<>();
        for (Class<?> type : allTypes) {
            typeProperties.put(type, getBeanProperties(type));
        }
        for (Class<?> type : allTypes) {
            createMetadata(type, typeProperties.get(type), typeProperties.keySet());
        }
    }

    public Collection<NodeMetadata> getRegisteredNodeMetadata() {
        return nodeMetadataByType.values();
    }

    public NodeMetadata getNodeMetadata(Class<?> type) {
        NodeMetadata nodeMetadata = nodeMetadataByType.get(type);
        if (nodeMetadata == null) {
            throw new CdoManagerException("Cannot resolve metadata for type " + type.getName() + ".");
        }
        return nodeMetadata;
    }

    public NodeMetadata getNodeMetadata(org.neo4j.graphdb.Label label) {
        return nodeMetadataByAggregatedLabels.get(label);
    }

    private void createMetadata(Class<?> type, Map<String, BeanProperty> beanProperties, Set<Class<?>> types) {
        LOGGER.debug("Processing type {}", type.getName());
        Map<String, AbstractPropertyMetadata> propertyMetadataMap = new HashMap<>();
        PrimitivePropertyMetadata indexedProperty = null;
        for (BeanProperty beanProperty : beanProperties.values()) {
            AbstractPropertyMetadata propertyMetadata;
            if (Collection.class.isAssignableFrom(beanProperty.getType())) {
                propertyMetadata = new CollectionPropertyMetadata(beanProperty, getRelationshipType(beanProperty));
            } else if (types.contains(beanProperty.getType())) {
                propertyMetadata = new ReferencePropertyMetadata(beanProperty, getRelationshipType(beanProperty));
            } else {
                Property property = beanProperty.getGetter().getAnnotation(Property.class);
                String propertyName = property != null ? property.value() : beanProperty.getName();
                if (Enum.class.isAssignableFrom(beanProperty.getType()) && property == null) {
                    propertyMetadata = new EnumPropertyMetadata(beanProperty, (Class<? extends Enum<?>>) beanProperty.getType());
                } else {
                    boolean indexed = beanProperty.getGetter().isAnnotationPresent(Indexed.class);
                    propertyMetadata = new PrimitivePropertyMetadata(beanProperty, propertyName);
                    if (indexed) {
                        indexedProperty = (PrimitivePropertyMetadata) propertyMetadata;
                    }
                }
            }
            propertyMetadataMap.put(propertyMetadata.getBeanProperty().getName(), propertyMetadata);
        }
        Label labelAnnotation = type.getAnnotation(Label.class);
        SortedSet<org.neo4j.graphdb.Label> aggregatedLabels = new TreeSet<>(new Comparator<org.neo4j.graphdb.Label>() {
            @Override
            public int compare(org.neo4j.graphdb.Label o1, org.neo4j.graphdb.Label o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        org.neo4j.graphdb.Label label = null;
        if (labelAnnotation != null) {
            label = DynamicLabel.label(labelAnnotation.value());
            aggregatedLabels.add(label);
            Class<?> usingIndexOf = labelAnnotation.usingIndexOf();
            if (!Object.class.equals(usingIndexOf)) {
                indexedProperty = nodeMetadataByType.get(usingIndexOf).getIndexedProperty();
            }
        }
        Set<NodeMetadata> superNodeMetadataSet = new HashSet<>();
        for (Class<?> implementedInterface : type.getInterfaces()) {
            NodeMetadata superNodeMetadata = nodeMetadataByType.get(implementedInterface);
            superNodeMetadataSet.add(superNodeMetadata);
            aggregatedLabels.addAll(superNodeMetadata.getAggregatedLabels());
        }
        NodeMetadata nodeMetadata = new NodeMetadata(type, superNodeMetadataSet, label, aggregatedLabels, propertyMetadataMap, indexedProperty);
        LOGGER.info("Registering {}, labels={}.", type.getName(), aggregatedLabels);
        nodeMetadataByType.put(type, nodeMetadata);
        if (label != null) {
            NodeMetadata conflictingMetadata = nodeMetadataByAggregatedLabels.put(label, nodeMetadata);
            if (conflictingMetadata != null) {
                throw new CdoManagerException("Types " + nodeMetadata.getType().getName() + " and " + conflictingMetadata.getType().getName() + " define the same label: " + label);
            }
        }
    }

    private RelationshipType getRelationshipType(BeanProperty beanProperty) {
        Relation relation = beanProperty.getGetter().getAnnotation(Relation.class);
        String name = relation != null ? relation.value() : beanProperty.getName();
        return DynamicRelationshipType.withName(name);
    }

    private Map<String, BeanProperty> getBeanProperties(Class<?> type) {
        if (!type.isInterface()) {
            throw new CdoManagerException("Type " + type.getName() + " is not an interface.");
        }
        Map<String, BeanProperty> beanProperties = new HashMap<>();
        getBeanProperties(type, beanProperties);
        return beanProperties;
    }

    private void getBeanProperties(Class<?> interfaceType, Map<String, BeanProperty> beanProperties) {
        for (Method method : interfaceType.getDeclaredMethods()) {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Type[] genericParameterTypes = method.getGenericParameterTypes();

            if (parameterTypes.length == 0 && !void.class.equals(genericReturnType)) {
                if (methodName.startsWith("get")) {
                    getBeanProperty(beanProperties, StringUtils.capitalize(methodName.substring(3)), returnType, genericReturnType).setGetter(method);
                } else if (methodName.startsWith("is")) {
                    getBeanProperty(beanProperties, StringUtils.capitalize(methodName.substring(2)), returnType, genericReturnType).setGetter(method);
                }
            } else if (parameterTypes.length == 1 && void.class.equals(genericReturnType) && methodName.startsWith("set")) {
                getBeanProperty(beanProperties, StringUtils.capitalize(methodName.substring(3)), parameterTypes[0], genericParameterTypes[0]).setSetter(method);
            } else {
                throw new CdoManagerException("Method " + method.toGenericString() + " is neither Getter nor Setter.");
            }
        }
    }

    private BeanProperty getBeanProperty(Map<String, BeanProperty> beanProperties, String propertyName, Class<?> type, Type genericType) {
        BeanProperty beanProperty = beanProperties.get(propertyName);
        if (beanProperty == null) {
            beanProperty = new BeanProperty(propertyName, type, genericType);
            beanProperties.put(beanProperty.getName(), beanProperty);
        }
        return beanProperty;
    }

}
