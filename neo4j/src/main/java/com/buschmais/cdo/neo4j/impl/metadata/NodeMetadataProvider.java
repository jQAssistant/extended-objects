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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static com.buschmais.cdo.neo4j.impl.metadata.BeanPropertyMethod.MethodType.GETTER;
import static com.buschmais.cdo.neo4j.impl.metadata.BeanPropertyMethod.MethodType.SETTER;

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
        Map<Class<?>, Collection<BeanPropertyMethod>> typeProperties = new HashMap<>();
        for (Class<?> type : allTypes) {
            if (!type.isInterface()) {
                throw new CdoManagerException("Type " + type.getName() + " is not an interface.");
            }
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

    private void createMetadata(Class<?> type, Collection<BeanPropertyMethod> beanMethods, Set<Class<?>> types) {
        LOGGER.debug("Processing type {}", type.getName());
        Collection<AbstractMethodMetadata> methodMetadataList = new ArrayList<>();

        // Collect the getter methods as they provide annotations holding meta information also to be applied to setters
        Map<String, BeanPropertyMethod> getterMethods = new HashMap<>();
        for (BeanPropertyMethod beanMethod : beanMethods) {
            if (BeanPropertyMethod.MethodType.GETTER.equals(beanMethod.getMethodType())) {
                getterMethods.put(beanMethod.getName(), beanMethod);
            }
        }
        PrimitiveMethodMetadata indexedProperty = null;
        for (BeanPropertyMethod beanPropertyMethod : beanMethods) {
            AbstractMethodMetadata propertyMetadata;
            if (Collection.class.isAssignableFrom(beanPropertyMethod.getType())) {
                propertyMetadata = new CollectionMethodMetadata(beanPropertyMethod, getRelationshipType(beanPropertyMethod, getterMethods));
            } else if (types.contains(beanPropertyMethod.getType())) {
                propertyMetadata = new ReferenceMethodMetadata(beanPropertyMethod, getRelationshipType(beanPropertyMethod, getterMethods));
            } else {
                Property property = getAnnotation(Property.class, beanPropertyMethod, getterMethods );
                String propertyName = property != null ? property.value() : beanPropertyMethod.getName();
                if (Enum.class.isAssignableFrom(beanPropertyMethod.getType()) && property == null) {
                    propertyMetadata = new EnumMethodMetadata(beanPropertyMethod, (Class<? extends Enum<?>>) beanPropertyMethod.getType());
                } else {
                    boolean indexed = beanPropertyMethod.getMethod().isAnnotationPresent(Indexed.class);
                    propertyMetadata = new PrimitiveMethodMetadata(beanPropertyMethod, propertyName);
                    if (indexed) {
                        indexedProperty = (PrimitiveMethodMetadata) propertyMetadata;
                    }
                }
            }
            methodMetadataList.add(propertyMetadata);
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
        for (Class<?> implementedInterface : type.getInterfaces()) {
            NodeMetadata superNodeMetadata = nodeMetadataByType.get(implementedInterface);
            aggregatedLabels.addAll(superNodeMetadata.getAggregatedLabels());
        }
        NodeMetadata nodeMetadata = new NodeMetadata(type, label, aggregatedLabels, methodMetadataList, indexedProperty);
        LOGGER.info("Registering {}, labels={}.", type.getName(), aggregatedLabels);
        nodeMetadataByType.put(type, nodeMetadata);
        if (label != null) {
            NodeMetadata conflictingMetadata = nodeMetadataByAggregatedLabels.put(label, nodeMetadata);
            if (conflictingMetadata != null) {
                throw new CdoManagerException("Types " + nodeMetadata.getType().getName() + " and " + conflictingMetadata.getType().getName() + " define the same label: " + label);
            }
        }
    }

    private RelationshipType getRelationshipType(BeanPropertyMethod beanPropertyMethod, Map<String, BeanPropertyMethod> getterMethods) {
        Relation relation = getAnnotation(Relation.class, beanPropertyMethod, getterMethods );
        String name = relation != null ? relation.value() : beanPropertyMethod.getName();
        return DynamicRelationshipType.withName(name);
    }

    private <T extends Annotation> T getAnnotation(Class<T> type, BeanPropertyMethod beanPropertyMethod, Map<String, BeanPropertyMethod> getters) {
        BeanPropertyMethod beanProperty = getters.get(beanPropertyMethod.getName());
        if (beanProperty == null) {
            beanProperty = beanPropertyMethod;
        }
        Method method = beanProperty.getMethod();
        return method.getAnnotation(type);
    }

    private Collection<BeanPropertyMethod> getBeanProperties(Class<?> type) {
        List<BeanPropertyMethod> beanProperties = new ArrayList<>();
        for (Method method : type.getDeclaredMethods()) {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0 && !void.class.equals(returnType)) {
                if (methodName.startsWith("get")) {
                    beanProperties.add(new BeanPropertyMethod(method, GETTER, StringUtils.capitalize(methodName.substring(3)), returnType));
                } else if (methodName.startsWith("is")) {
                    beanProperties.add(new BeanPropertyMethod(method, GETTER, StringUtils.capitalize(methodName.substring(2)), returnType));
                }
            } else if (parameterTypes.length == 1 && void.class.equals(returnType) && methodName.startsWith("set")) {
                beanProperties.add(new BeanPropertyMethod(method, SETTER, StringUtils.capitalize(methodName.substring(3)), parameterTypes[0]));
            } else {
                throw new CdoManagerException("Method " + method.toGenericString() + " is neither Getter nor Setter.");
            }
        }
        return beanProperties;
    }

}
