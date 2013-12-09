package com.buschmais.cdo.neo4j.impl.node.metadata;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.api.annotation.ImplementedBy;
import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.impl.common.DependencyResolver;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.BeanMethodProvider;
import com.buschmais.cdo.neo4j.impl.common.reflection.PropertyMethod;
import com.buschmais.cdo.neo4j.impl.common.reflection.UserMethod;
import com.buschmais.cdo.neo4j.spi.Datastore;
import com.buschmais.cdo.neo4j.spi.DatastoreMetadataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MetadataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeMetadata.class);
    private Datastore.DatastoreMetadataFactory<?> metadataFactory;
    private DatastoreMetadataProvider datastoreMetadataProvider;

    private Map<Class<?>, TypeMetadata> entityMetadataByType = new HashMap<>();

    public MetadataProvider(Collection<Class<?>> types, Datastore<?> datastore) {
        this.metadataFactory = datastore.getMetadataFactory();
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
        datastoreMetadataProvider = datastore.createMetadataProvider(entityMetadataByType.values());
        entityMetadataByType.put(CompositeObject.class, new TypeMetadata(CompositeObject.class, Collections.<AbstractMethodMetadata>emptyList(), null, null));

    }

    public DatastoreMetadataProvider getDatastoreMetadataProvider() {
        return datastoreMetadataProvider;
    }

    public Collection<TypeMetadata> getRegisteredMetadata() {
        return entityMetadataByType.values();
    }

    public TypeMetadata getEntityMetadata(Class<?> type) {
        TypeMetadata typeMetadata = entityMetadataByType.get(type);
        if (typeMetadata == null) {
            throw new CdoException("Cannot resolve metadata for type " + type.getName() + ".");
        }
        return typeMetadata;
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
                methodMetadata = new ImplementedByMethodMetadata(beanMethod, implementedBy.value(), metadataFactory.createImplementedByMetadata(beanMethod));
            } else if (resultOf != null) {
                methodMetadata = createResultOfMetadata(beanMethod, resultOf);
            } else if (beanMethod instanceof PropertyMethod) {
                methodMetadata = createPropertyMethodMetadata(types, (PropertyMethod) beanMethod);
            } else {
                methodMetadata = new UnsupportedOperationMethodMetadata((UserMethod) beanMethod);
            }
            Indexed indexedAnnotation = beanMethod.getAnnotation(Indexed.class);
            if (indexedAnnotation != null) {
                if (!(methodMetadata instanceof PrimitivePropertyMethodMetadata)) {
                    throw new CdoException("Only primitve properties are allowed to be annotated with " + Indexed.class.getName());
                }
                indexedProperty = new IndexedPropertyMethodMetadata((PropertyMethod) beanMethod, (PrimitivePropertyMethodMetadata) methodMetadata, indexedAnnotation.create(), metadataFactory.createIndexedPropertyMetadata((PropertyMethod) beanMethod));
            }
            methodMetadataList.add(methodMetadata);
        }
        TypeMetadata typeMetadata = new TypeMetadata(type, methodMetadataList, indexedProperty, metadataFactory.createEntityMetadata(type, entityMetadataByType));
        entityMetadataByType.put(type, typeMetadata);
    }

    private AbstractMethodMetadata createPropertyMethodMetadata(Set<Class<?>> types, PropertyMethod beanPropertyMethod) {
        AbstractMethodMetadata methodMetadata;
        if (Collection.class.isAssignableFrom(beanPropertyMethod.getType())) {
            methodMetadata = new CollectionPropertyMethodMetadata(beanPropertyMethod, new RelationMetadata(metadataFactory.createRelationMetadata(beanPropertyMethod)), metadataFactory.getRelationDirection(beanPropertyMethod), metadataFactory.createCollectionPropertyMetadata(beanPropertyMethod));
        } else if (types.contains(beanPropertyMethod.getType())) {
            methodMetadata = new ReferencePropertyMethodMetadata(beanPropertyMethod, new RelationMetadata(metadataFactory.createRelationMetadata(beanPropertyMethod)), metadataFactory.getRelationDirection(beanPropertyMethod), metadataFactory.createReferencePropertyMetadata(beanPropertyMethod));
        } else {
            if (Enum.class.isAssignableFrom(beanPropertyMethod.getType())) {
                methodMetadata = new EnumPropertyMethodMetadata(beanPropertyMethod, beanPropertyMethod.getType(), metadataFactory.createEnumPropertyMetadata(beanPropertyMethod));
            } else {
                methodMetadata = new PrimitivePropertyMethodMetadata(beanPropertyMethod, metadataFactory.createPrimitvePropertyMetadata(beanPropertyMethod));
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
