package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.api.annotation.ImplementedBy;
import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.impl.common.DependencyResolver;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.UserDefinedMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethodProvider;
import com.buschmais.cdo.neo4j.spi.Datastore;
import org.neo4j.graphdb.DynamicLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MetadataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityMetadata.class);
    private Datastore<?> datastore;
    private Map<Class<?>, EntityMetadata> entityMetadataByType = new HashMap<>();
    private Map<org.neo4j.graphdb.Label, Set<EntityMetadata>> entityMetadataByLabel = new HashMap<>();

    public MetadataProvider(Collection<Class<?>> types, Datastore<?> datastore) {
        this.datastore = datastore;
        DependencyResolver.DependencyProvider<Class<?>> classDependencyProvider = new DependencyResolver.DependencyProvider<Class<?>>() {
            @Override
            public Set<Class<?>> getDependencies(Class<?> dependent) {
                return new HashSet<>(Arrays.asList(dependent.getInterfaces()));
            }
        };
        List<Class<?>> allTypes = DependencyResolver.newInstance(types, classDependencyProvider).resolve();
        LOGGER.debug("Processing types {}", allTypes);
        BeanMethodProvider beanMethodProvider = BeanMethodProvider.newInstance();
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

    public Collection<EntityMetadata> getRegisteredNodeMetadata() {
        return entityMetadataByType.values();
    }

    public EntityMetadata getEntityMetadata(Class<?> type) {
        EntityMetadata entityMetadata = entityMetadataByType.get(type);
        if (entityMetadata == null) {
            throw new CdoException("Cannot resolve metadata for type " + type.getName() + ".");
        }
        return entityMetadata;
    }

    public Set<EntityMetadata> getEntityMetadata(org.neo4j.graphdb.Label label) {
        return entityMetadataByLabel.get(label);
    }

    private void createMetadata(Class<?> type, Collection<BeanMethod> beanMethods, Set<Class<?>> types) {
        LOGGER.debug("Processing type {}", type.getName());
        Collection<AbstractMethodMetadata> methodMetadataList = new ArrayList<>();
        // Collect the getter methods as they provide annotations holding meta information also to be applied to setters
        IndexedPropertyMethodMetadata indexedProperty = null;
        for (BeanMethod beanMethod : beanMethods) {
            AbstractMethodMetadata methodMetadata;
            ResultOf resultOf = beanMethod.getAnnotation(ResultOf.class);
            ImplementedBy implementedBy = beanMethod.getAnnotation(ImplementedBy.class);
            if (implementedBy != null) {
                methodMetadata = new ImplementedByMethodMetadata(beanMethod, implementedBy.value(), datastore.createImplementedByMetadata(beanMethod));
            } else if (resultOf != null) {
                methodMetadata = createResultOfMetadata(beanMethod, resultOf);
            } else if (beanMethod instanceof PropertyMethod) {
                methodMetadata = createPropertyMethodMetadata(types, (PropertyMethod) beanMethod);
            } else {
                methodMetadata = new UnsupportedOperationMethodMetadata((UserDefinedMethod) beanMethod);
            }
            Indexed indexedAnnotation = beanMethod.getAnnotation(Indexed.class);
            if (indexedAnnotation != null) {
                if (!(methodMetadata instanceof PrimitivePropertyMethodMetadata)) {
                    throw new CdoException("Only primitve properties are allowed to be annotated with " + Indexed.class.getName());
                }
                indexedProperty = new IndexedPropertyMethodMetadata((PropertyMethod) beanMethod, (PrimitivePropertyMethodMetadata) methodMetadata, indexedAnnotation.create(), datastore.createIndexedPropertyMetadata((PropertyMethod) beanMethod));
            }
            methodMetadataList.add(methodMetadata);
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
                indexedProperty = entityMetadataByType.get(usingIndexOf).getIndexedProperty();
            }
        }
        for (Class<?> implementedInterface : type.getInterfaces()) {
            EntityMetadata superEntityMetadata = entityMetadataByType.get(implementedInterface);
            aggregatedLabels.addAll(superEntityMetadata.getAggregatedLabels());
        }
        EntityMetadata<?> entityMetadata = new EntityMetadata(type, label, aggregatedLabels, methodMetadataList, indexedProperty, datastore.createEntityMetadata(type));
        // determine all possible metadata for a label
        for (org.neo4j.graphdb.Label aggregatedLabel : entityMetadata.getAggregatedLabels()) {
            Set<EntityMetadata> entityMetadataOfLabel = entityMetadataByLabel.get(aggregatedLabel);
            if (entityMetadataOfLabel == null) {
                entityMetadataOfLabel = new HashSet<>();
                entityMetadataByLabel.put(label, entityMetadataOfLabel);
            }
            entityMetadataOfLabel.add(entityMetadata);
        }
        LOGGER.info("Registering {}, labels={}.", type.getName(), aggregatedLabels);
        entityMetadataByType.put(type, entityMetadata);
        entityMetadataByType.put(CompositeObject.class, new EntityMetadata(CompositeObject.class, null, Collections.<org.neo4j.graphdb.Label>emptySet(), Collections.<AbstractMethodMetadata>emptyList(), null, null));
    }

    private AbstractMethodMetadata createPropertyMethodMetadata(Set<Class<?>> types, PropertyMethod beanPropertyMethod) {
        AbstractMethodMetadata methodMetadata;
        if (Collection.class.isAssignableFrom(beanPropertyMethod.getType())) {
            methodMetadata = new CollectionPropertyMethodMetadata(beanPropertyMethod, new RelationMetadata(datastore.createRelationMetadata(beanPropertyMethod)), datastore.getRelationDirection(beanPropertyMethod), datastore.createCollectionPropertyMetadata(beanPropertyMethod));
        } else if (types.contains(beanPropertyMethod.getType())) {
            methodMetadata = new ReferencePropertyMethodMetadata(beanPropertyMethod, new RelationMetadata(datastore.createRelationMetadata(beanPropertyMethod)), datastore.getRelationDirection(beanPropertyMethod), datastore.createReferencePropertyMetadata(beanPropertyMethod));
        } else {
            if (Enum.class.isAssignableFrom(beanPropertyMethod.getType())) {
                methodMetadata = new EnumPropertyMethodMetadata(beanPropertyMethod, beanPropertyMethod.getType(), datastore.createEnumPropertyMetadata(beanPropertyMethod));
            } else {
                methodMetadata = new PrimitivePropertyMethodMetadata(beanPropertyMethod, datastore.createPrimitvePropertyMetadata(beanPropertyMethod));
            }
        }
        return methodMetadata;
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
}
