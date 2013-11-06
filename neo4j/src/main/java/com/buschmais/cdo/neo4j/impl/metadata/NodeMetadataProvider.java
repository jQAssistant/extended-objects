package com.buschmais.cdo.neo4j.impl.metadata;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.annotation.Indexed;
import com.buschmais.cdo.neo4j.annotation.Label;
import com.buschmais.cdo.neo4j.annotation.Property;
import com.buschmais.cdo.neo4j.annotation.Relation;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class NodeMetadataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMetadata.class);
    private Set<org.neo4j.graphdb.Label> allLabels = new HashSet<>();
    private Map<Class<?>, NodeMetadata> nodeMetadataByType = new HashMap<>();
    private Map<Set<org.neo4j.graphdb.Label>, NodeMetadata> nodeMetadataByAggregatedLabels = new HashMap<>();

    public NodeMetadataProvider(Collection<Class<?>> types) {
        DependencyResolver.DependencyProvider<Class<?>> classDependencyProvider = new DependencyResolver.DependencyProvider<Class<?>>() {

            @Override
            public Set<Class<?>> getDependencies(Class<?> dependent) {
                return new HashSet<>(Arrays.asList(dependent.getInterfaces()));
            }
        };
        List<Class<?>> sortedTypes = DependencyResolver.newInstance(types, classDependencyProvider).resolve();
        LOGGER.info("Registering types {}", sortedTypes);
        Map<Class<?>, Map<String, BeanProperty>> typeProperties = new HashMap<>();
        for (Class<?> type : sortedTypes) {
            typeProperties.put(type, getBeanProperties(type));
        }
        for (Class<?> type : sortedTypes) {
            createMetadata(type, typeProperties.get(type), typeProperties.keySet());
        }
    }

    public Collection<NodeMetadata> getRegisteredNodeMetadata() {
        return nodeMetadataByType.values();
    }

    public NodeMetadata getNodeMetadata(Class<?> type) {
        NodeMetadata nodeMetadata = nodeMetadataByType.get(type);
        if (nodeMetadata == null) {
            throw new CdoManagerException("Cannot find metadata for type " + type.getName() + ".");
        }
        return nodeMetadata;
    }

    public NodeMetadata getNodeMetadata(Set<org.neo4j.graphdb.Label> aggregatedLabels) {
        NodeMetadata nodeMetadata = nodeMetadataByAggregatedLabels.get(aggregatedLabels);
        if (nodeMetadata == null) {
            throw new CdoManagerException("Cannot find metadata for labels  " + aggregatedLabels + ".");
        }
        return nodeMetadata;
    }

    public Set<org.neo4j.graphdb.Label> getAllLabels() {
        return allLabels;
    }

    private void addImplementedInterfaces(Class<?> type, Set<Class<?>> types) {
        types.add(type);
        for (Class<?> implementedInterface : type.getInterfaces()) {
            addImplementedInterfaces(implementedInterface, types);
        }
    }

    private void createMetadata(Class<?> type, Map<String, BeanProperty> beanProperties, Set<Class<?>> types) {
        LOGGER.info("Creating node meta for {}", type.getName());
        Map<String, AbstractPropertyMetadata> propertyMetadataMap = new HashMap<>();
        PrimitivePropertyMetadata indexedProperty = null;
        for (BeanProperty beanProperty : beanProperties.values()) {
            AbstractPropertyMetadata propertyMetadata;
            if (Collection.class.isAssignableFrom(beanProperty.getType())) {
                ParameterizedType parameterizedType = (ParameterizedType) beanProperty.getGenericType();
                Type elementType = parameterizedType.getActualTypeArguments()[0];
                propertyMetadata = new CollectionPropertyMetadata(beanProperty, getRelationshipType(beanProperty));
            } else if (types.contains(beanProperty.getType())) {
                propertyMetadata = new ReferencePropertyMetadata(beanProperty, getRelationshipType(beanProperty));
            } else if (Enum.class.isAssignableFrom(beanProperty.getType())) {
                propertyMetadata = new EnumPropertyMetadata(beanProperty, (Class<? extends Enum<?>>) beanProperty.getType());
            } else {
                Property property = beanProperty.getGetter().getAnnotation(Property.class);
                String propertyName = property != null ? property.value() : beanProperty.getName();
                propertyMetadata = new PrimitivePropertyMetadata(beanProperty, propertyName);
                if (beanProperty.getGetter().isAnnotationPresent(Indexed.class)) {
                    indexedProperty = (PrimitivePropertyMetadata) propertyMetadata;
                }
            }
            propertyMetadataMap.put(propertyMetadata.getBeanProperty().getName(), propertyMetadata);
        }
        Label labelAnnotation = type.getAnnotation(Label.class);
        Set<org.neo4j.graphdb.Label> aggregatedLabels = new HashSet<>();
        org.neo4j.graphdb.Label label = null;
        if (labelAnnotation != null) {
            label = DynamicLabel.label(labelAnnotation.value());
            aggregatedLabels.add(label);
            allLabels.add(label);
        }
        Set<NodeMetadata> superNodeMetadataSet = new HashSet<>();
        for (Class<?> implementedInterface : type.getInterfaces()) {
            NodeMetadata superNodeMetadata = nodeMetadataByType.get(implementedInterface);
            superNodeMetadataSet.add(superNodeMetadata);
            org.neo4j.graphdb.Label superNodeMetadataLabel = superNodeMetadata.getLabel();
            if (superNodeMetadataLabel != null) {
                aggregatedLabels.add(superNodeMetadataLabel);
            }
        }
        NodeMetadata nodeMetadata = new NodeMetadata(type, superNodeMetadataSet, label, aggregatedLabels, propertyMetadataMap, indexedProperty);
        nodeMetadataByType.put(type, nodeMetadata);
        if (label != null) {
            nodeMetadataByAggregatedLabels.put(aggregatedLabels, nodeMetadata);
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
