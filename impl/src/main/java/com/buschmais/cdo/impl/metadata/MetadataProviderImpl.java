package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.api.annotation.ImplementedBy;
import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.impl.MetadataProvider;
import com.buschmais.cdo.impl.reflection.BeanMethodProvider;
import com.buschmais.cdo.spi.annotation.IndexDefinition;
import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.spi.metadata.*;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.AnnotatedType;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.spi.reflection.UserMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class MetadataProviderImpl<EntityMetadata extends DatastoreEntityMetadata<Discriminator>, Discriminator> implements MetadataProvider<EntityMetadata, Discriminator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeMetadata.class);
    private DatastoreMetadataFactory<EntityMetadata, Discriminator> metadataFactory;
    private TypeMetadataResolver<EntityMetadata, Discriminator> typeMetadataResolver;

    private Map<Class<?>, TypeMetadata<EntityMetadata>> entityMetadataByType = new HashMap<>();

    public MetadataProviderImpl(Collection<Class<?>> types, Datastore<?, EntityMetadata, Discriminator> datastore) {
        this.metadataFactory = datastore.getMetadataFactory();
        DependencyResolver.DependencyProvider<Class<?>> classDependencyProvider = new DependencyResolver.DependencyProvider<Class<?>>() {
            @Override
            public Set<Class<?>> getDependencies(Class<?> dependent) {
                return new HashSet<>(Arrays.asList(dependent.getInterfaces()));
            }
        };
        List<Class<?>> allClasses = DependencyResolver.newInstance(types, classDependencyProvider).resolve();
        LOGGER.debug("Processing types {}", allClasses);
        Map<Class<?>, Collection<AnnotatedMethod>> annotatedMethodsByClass = new HashMap<>();
        for (Class<?> currentClass : allClasses) {
            if (!currentClass.isInterface()) {
                throw new CdoException("Type " + currentClass.getName() + " is not an interface.");
            }
            annotatedMethodsByClass.put(currentClass, BeanMethodProvider.newInstance().getMethods(currentClass));
        }
        List<TypeMetadata> typeMetadata = new ArrayList<>();
        for (Class<?> currentClass : allClasses) {
            AnnotatedType annotatedType = new AnnotatedType(currentClass);
            TypeMetadata metadata = createMetadata(annotatedType, annotatedMethodsByClass.get(currentClass), annotatedMethodsByClass.keySet());
            entityMetadataByType.put(currentClass, metadata);
            typeMetadata.add(metadata);
        }
        typeMetadataResolver = new TypeMetadataResolver(entityMetadataByType);
        entityMetadataByType.put(CompositeObject.class, new TypeMetadata(new AnnotatedType(CompositeObject.class), Collections.emptyList(), Collections.<AbstractMethodMetadata>emptyList(), null, null));

    }

    @Override
    public TypeMetadataSet getTypes(Set<Discriminator> discriminators) {
        return typeMetadataResolver.getTypes(discriminators);
    }

    @Override
    public Set<Discriminator> getDiscriminators(TypeMetadataSet<EntityMetadata> types) {
        Set<Discriminator> discriminators = new HashSet<>();
        for (TypeMetadata<EntityMetadata> typeMetadata : types) {
            Set<Discriminator> discriminatorsOfType = typeMetadataResolver.getDiscriminators(typeMetadata);
            discriminators.addAll(discriminatorsOfType);
        }
        return discriminators;
    }

    @Override
    public Collection<TypeMetadata<EntityMetadata>> getRegisteredMetadata() {
        return entityMetadataByType.values();
    }

    @Override
    public TypeMetadata getEntityMetadata(Class<?> type) {
        TypeMetadata typeMetadata = entityMetadataByType.get(type);
        if (typeMetadata == null) {
            throw new CdoException("Cannot resolve metadata for type " + type.getName() + ".");
        }
        return typeMetadata;
    }

    private TypeMetadata createMetadata(AnnotatedType annotatedType, Collection<AnnotatedMethod> annotatedMethods, Set<Class<?>> types) {
        LOGGER.debug("Processing class {}", annotatedType.getAnnotatedElement().getName());
        Collection<AbstractMethodMetadata> methodMetadataList = new ArrayList<>();
        // Collect the getter methods as they provide annotations holding meta information also to be applied to setters
        IndexedPropertyMethodMetadata indexedProperty = null;
        for (AnnotatedMethod annotatedMethod : annotatedMethods) {
            AbstractMethodMetadata methodMetadata;
            ResultOf resultOf = annotatedMethod.getAnnotation(ResultOf.class);
            ImplementedBy implementedBy = annotatedMethod.getAnnotation(ImplementedBy.class);
            if (implementedBy != null) {
                methodMetadata = new ImplementedByMethodMetadata(annotatedMethod, implementedBy.value(), metadataFactory.createImplementedByMetadata(annotatedMethod));
            } else if (resultOf != null) {
                methodMetadata = createResultOfMetadata(annotatedMethod, resultOf);
            } else if (annotatedMethod instanceof PropertyMethod) {
                methodMetadata = createPropertyMethodMetadata(types, (PropertyMethod) annotatedMethod);
            } else {
                methodMetadata = new UnsupportedOperationMethodMetadata((UserMethod) annotatedMethod);
            }
            Annotation indexedAnnotation = annotatedMethod.getByMetaAnnotation(IndexDefinition.class);
            if (indexedAnnotation != null) {
                if (!(methodMetadata instanceof PrimitivePropertyMethodMetadata)) {
                    throw new CdoException("Only primitive properties are allowed to be used for indexing.");
                }
                indexedProperty = new IndexedPropertyMethodMetadata((PropertyMethod) annotatedMethod, (PrimitivePropertyMethodMetadata) methodMetadata, metadataFactory.createIndexedPropertyMetadata((PropertyMethod) annotatedMethod));
            }
            methodMetadataList.add(methodMetadata);
        }
        List<TypeMetadata<EntityMetadata>> superTypes = new ArrayList<>();
        for (Class<?> i : annotatedType.getAnnotatedElement().getInterfaces()) {
            superTypes.add(this.entityMetadataByType.get(i));
        }
        DatastoreEntityMetadata<Discriminator> datastoreEntityMetadata = metadataFactory.createEntityMetadata(annotatedType, entityMetadataByType);
        TypeMetadata typeMetadata = new TypeMetadata(annotatedType, superTypes, methodMetadataList, indexedProperty, datastoreEntityMetadata);
        return typeMetadata;
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

    private AbstractMethodMetadata createResultOfMetadata(AnnotatedMethod annotatedMethod, ResultOf resultOf) {
        Method method = annotatedMethod.getAnnotatedElement();
        // Determine query type
        Class<?> queryType = resultOf.query();
        Class<?> returnType = method.getReturnType();
        if (Object.class.equals(queryType)) {
            if (Iterable.class.isAssignableFrom(returnType)) {
                java.lang.reflect.Type genericReturnType = method.getGenericReturnType();
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
        return new ResultOfMethodMetadata(annotatedMethod, queryType, resultOf.usingThisAs(), parameters, singleResult);
    }
}
