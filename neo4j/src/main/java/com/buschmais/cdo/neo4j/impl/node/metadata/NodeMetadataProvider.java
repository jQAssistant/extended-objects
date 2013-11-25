package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.api.annotation.*;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethodProvider;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanPropertyMethod;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.buschmais.cdo.neo4j.impl.common.DependencyResolver.DependencyProvider;

public class NodeMetadataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMetadata.class);
    private BeanMethodProvider beanMethodProvider = new BeanMethodProvider();
    private Map<Class<?>, NodeMetadata> nodeMetadataByType = new HashMap<>();
    private Map<org.neo4j.graphdb.Label, Set<NodeMetadata>> nodeMetadataByLabel = new HashMap<>();

    public NodeMetadataProvider(Collection<Class<?>> types) {
        DependencyProvider<Class<?>> classDependencyProvider = new DependencyProvider<Class<?>>() {
            @Override
            public Set<Class<?>> getDependencies(Class<?> dependent) {
                return new HashSet<>(Arrays.asList(dependent.getInterfaces()));
            }
        };
        List<Class<?>> allTypes = com.buschmais.cdo.neo4j.impl.common.DependencyResolver.newInstance(types, classDependencyProvider).resolve();
        LOGGER.debug("Processing types {}", allTypes);
        Map<Class<?>, Collection<BeanMethod>> typeMethods = new HashMap<>();
        for (Class<?> type : allTypes) {
            if (!type.isInterface()) {
                throw new CdoException("Type " + type.getName() + " is not an interface.");
            }
            typeMethods.put(type, beanMethodProvider.getMethods(type));
        }
        for (Class<?> type : allTypes) {
            createMetadata(type, typeMethods.get(type), typeMethods.keySet());
        }
    }

    public Collection<NodeMetadata> getRegisteredNodeMetadata() {
        return nodeMetadataByType.values();
    }

    public NodeMetadata getNodeMetadata(Class<?> type) {
        NodeMetadata nodeMetadata = nodeMetadataByType.get(type);
        if (nodeMetadata == null) {
            throw new CdoException("Cannot resolve metadata for type " + type.getName() + ".");
        }
        return nodeMetadata;
    }

    public Set<NodeMetadata> getNodeMetadata(org.neo4j.graphdb.Label label) {
        return nodeMetadataByLabel.get(label);
    }

    private void createMetadata(Class<?> type, Collection<BeanMethod> beanMethods, Set<Class<?>> types) {
        LOGGER.debug("Processing type {}", type.getName());
        Collection<AbstractMethodMetadata> methodMetadataList = new ArrayList<>();

        // Collect the getter methods as they provide annotations holding meta information also to be applied to setters
        Map<String, BeanPropertyMethod> getterMethods = new HashMap<>();
        for (BeanMethod beanMethod : beanMethods) {
            if (beanMethod instanceof BeanPropertyMethod) {
                BeanPropertyMethod beanPropertyMethod = (BeanPropertyMethod) beanMethod;
                if (BeanPropertyMethod.MethodType.GETTER.equals(beanPropertyMethod.getMethodType())) {
                    getterMethods.put(beanPropertyMethod.getName(), beanPropertyMethod);
                }
            }
        }
        PrimitivePropertyMethodMetadata indexedProperty = null;
        for (BeanMethod beanMethod : beanMethods) {
            AbstractMethodMetadata propertyMetadata;
            ResultOf resultOf = beanMethod.getMethod().getAnnotation(ResultOf.class);
            ImplementedBy implementedBy = beanMethod.getMethod().getAnnotation(ImplementedBy.class);
            if (implementedBy != null) {
                propertyMetadata = new ImplementedByMethodMetadata(beanMethod, implementedBy.value());
            } else if (resultOf != null) {
                propertyMetadata = createResultOfMetadata(beanMethod, resultOf);
            } else if (beanMethod instanceof BeanPropertyMethod) {
                BeanPropertyMethod beanPropertyMethod = (BeanPropertyMethod) beanMethod;
                if (Collection.class.isAssignableFrom(beanPropertyMethod.getType())) {
                    propertyMetadata = new CollectionPropertyMethodMetadata(beanPropertyMethod, getRelationshipType(beanPropertyMethod, getterMethods));
                } else if (types.contains(beanPropertyMethod.getType())) {
                    propertyMetadata = new ReferencePropertyMethodMetadata(beanPropertyMethod, getRelationshipType(beanPropertyMethod, getterMethods));
                } else {
                    Property property = getAnnotation(Property.class, beanPropertyMethod, getterMethods);
                    String propertyName = property != null ? property.value() : beanPropertyMethod.getName();
                    if (Enum.class.isAssignableFrom(beanPropertyMethod.getType()) && property == null) {
                        propertyMetadata = new EnumPropertyMethodMetadata(beanPropertyMethod, (Class<? extends Enum<?>>) beanPropertyMethod.getType());
                    } else {
                        boolean indexed = beanMethod.getMethod().isAnnotationPresent(Indexed.class);
                        propertyMetadata = new PrimitivePropertyMethodMetadata(beanPropertyMethod, propertyName);
                        if (indexed) {
                            indexedProperty = (PrimitivePropertyMethodMetadata) propertyMetadata;
                        }
                    }
                }
            } else {
                throw new CdoException("Cannot determine metadata of method " + beanMethod.getMethod().getName());
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
            Class<?> usingIndexOf = labelAnnotation.usingIndexedPropertyOf();
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
        for (org.neo4j.graphdb.Label aggregatedLabel : nodeMetadata.getAggregatedLabels()) {
            Set<NodeMetadata> nodeMetadataOfLabel = nodeMetadataByLabel.get(aggregatedLabel);
            if (nodeMetadataOfLabel == null) {
                nodeMetadataOfLabel = new HashSet<>();
                nodeMetadataByLabel.put(label, nodeMetadataOfLabel);
            }
            nodeMetadataOfLabel.add(nodeMetadata);
        }

        nodeMetadataByType.put(CompositeObject.class, new NodeMetadata(CompositeObject.class, null, Collections.<org.neo4j.graphdb.Label>emptySet(), Collections.<AbstractMethodMetadata>emptyList(), null));
    }

    private AbstractMethodMetadata createResultOfMetadata(BeanMethod beanMethod, ResultOf resultOf) {
        Method method = beanMethod.getMethod();
        // Determine query type
        Class<?> queryType = resultOf.query();
        Class<?> returnType = method.getReturnType();
        if (Object.class.equals(queryType)) {
            if (Iterable.class.isAssignableFrom(returnType)) {
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                    queryType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
            } else {
                queryType = returnType;
            }
        }
        // Determine parameter bindings
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<ResultOf.Parameter> parameters = new ArrayList<>();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            ResultOf.Parameter parameter = null;
            for (Annotation annotation : parameterAnnotations[i]) {
                if (ResultOf.Parameter.class.equals(annotation.annotationType())) {
                    parameter = (ResultOf.Parameter) annotation;
                }
            }
            if (parameter == null) {
                throw new CdoException("Cannot determine parameter names for '" + method.getName() + "', all parameters must be annotated with '" + ResultOf.Parameter.class.getName() + "'.");
            }
            parameters.add(parameter);
        }
        boolean singleResult = !Iterable.class.isAssignableFrom(returnType);
        return new ResultOfMethodMetadata(beanMethod, queryType, resultOf.usingThisAs(), parameters, singleResult);
    }

    private RelationshipType getRelationshipType(BeanPropertyMethod beanPropertyMethod, Map<String, BeanPropertyMethod> getterMethods) {
        Relation relation = getAnnotation(Relation.class, beanPropertyMethod, getterMethods);
        String name = relation != null ? relation.value() : StringUtils.capitalize(beanPropertyMethod.getName());
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
}
