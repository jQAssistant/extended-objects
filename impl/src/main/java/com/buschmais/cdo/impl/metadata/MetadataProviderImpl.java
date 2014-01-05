package com.buschmais.cdo.impl.metadata;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.api.annotation.ImplementedBy;
import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.impl.MetadataProvider;
import com.buschmais.cdo.impl.reflection.BeanMethodProvider;
import com.buschmais.cdo.spi.annotation.EntityDefinition;
import com.buschmais.cdo.spi.annotation.IndexDefinition;
import com.buschmais.cdo.spi.annotation.RelationDefinition;
import com.buschmais.cdo.spi.datastore.*;
import com.buschmais.cdo.spi.metadata.method.*;
import com.buschmais.cdo.spi.metadata.type.*;
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

/**
 * Implementation of the {@link MetadataProvider}.
 *
 * @param <EntityMetadata>        The type of datastore specific entity metadata.
 * @param <EntityDiscriminator>   The type of datastore specific entity type discriminators.
 * @param <RelationMetadata>      The type of datastore specific relation metadata.
 * @param <RelationDiscriminator> The type of datastore specific relationtype discriminators.
 */
public class MetadataProviderImpl<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeMetadata.class);
    private Map<Class<?>, Collection<AnnotatedMethod>> annotatedMethods;

    private DatastoreMetadataFactory<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataFactory;
    private EntityTypeMetadataResolver<EntityMetadata, EntityDiscriminator> entityTypeMetadataResolver;

    private Map<Class<?>, TypeMetadata> metadataByType = new HashMap<>();

    private Map<RelationTypeMetadata<RelationMetadata>, Class<?>> outgoingTypes = new HashMap<>();
    private Map<RelationTypeMetadata<RelationMetadata>, Class<?>> incomingTypes = new HashMap<>();

    /**
     * Constructor.
     *
     * @param types     All classes as provided by the CDO unit.
     * @param datastore The datastore.
     */
    public MetadataProviderImpl(Collection<Class<?>> types, Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> datastore) {
        this.metadataFactory = datastore.getMetadataFactory();
        DependencyResolver.DependencyProvider<Class<?>> classDependencyProvider = new DependencyResolver.DependencyProvider<Class<?>>() {
            @Override
            public Set<Class<?>> getDependencies(Class<?> dependent) {
                return new HashSet<>(Arrays.asList(dependent.getInterfaces()));
            }
        };
        List<Class<?>> allClasses = DependencyResolver.newInstance(types, classDependencyProvider).resolve();
        LOGGER.debug("Processing types {}", allClasses);
        this.annotatedMethods = new HashMap<>();
        for (Class<?> currentClass : allClasses) {
            if (!currentClass.isInterface()) {
                throw new CdoException("Type " + currentClass.getName() + " is not an interface.");
            }
            annotatedMethods.put(currentClass, BeanMethodProvider.newInstance().getMethods(currentClass));
        }
        for (Class<?> currentClass : allClasses) {
            getOrCreateTypeMetadata(currentClass);
        }
        entityTypeMetadataResolver = new EntityTypeMetadataResolver(metadataByType);
        metadataByType.put(CompositeObject.class, new SimpleTypeMetadata(new AnnotatedType(CompositeObject.class), Collections.<TypeMetadata>emptyList(), Collections.<MethodMetadata>emptyList()));
    }

    private TypeMetadata getOrCreateTypeMetadata(Class<?> type) {
        AnnotatedType annotatedType = new AnnotatedType(type);
        TypeMetadata typeMetadata = metadataByType.get(annotatedType.getAnnotatedElement());
        if (typeMetadata == null) {
            typeMetadata = createTypeMetadata(annotatedType);
            LOGGER.debug("Registering class {}", annotatedType.getName());
            metadataByType.put(annotatedType.getAnnotatedElement(), typeMetadata);
        }
        return typeMetadata;
    }

    private TypeMetadata createTypeMetadata(AnnotatedType annotatedType) {
        Class<?> currentClass = annotatedType.getAnnotatedElement();
        List<TypeMetadata> superTypes = getSuperTypeMetadata(annotatedType);
        Collection<AnnotatedMethod> annotatedMethods = this.annotatedMethods.get(currentClass);
        Collection<MethodMetadata> methodMetadataOfType = getMethodMetadataOfType(annotatedType, annotatedMethods);
        TypeMetadata metadata;
        if (isEntityType(annotatedType)) {
            metadata = createEntityTypeMetadata(annotatedType, superTypes, methodMetadataOfType);
        } else if (isRelationType(annotatedType)) {
            metadata = createRelationTypeMetadata(annotatedType, superTypes, methodMetadataOfType);
        } else {
            metadata = new SimpleTypeMetadata(annotatedType, superTypes, methodMetadataOfType);
        }
        return metadata;
    }

    @Override
    public TypeMetadataSet getTypes(Set<EntityDiscriminator> entityDiscriminators) {
        return entityTypeMetadataResolver.getTypes(entityDiscriminators);
    }

    @Override
    public Set<EntityDiscriminator> getDiscriminators(TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types) {
        Set<EntityDiscriminator> entityDiscriminators = new HashSet<>();
        for (EntityTypeMetadata<EntityMetadata> entityTypeMetadata : types) {
            Set<EntityDiscriminator> discriminatorsOfType = entityTypeMetadataResolver.getDiscriminators(entityTypeMetadata);
            entityDiscriminators.addAll(discriminatorsOfType);
        }
        return entityDiscriminators;
    }

    @Override
    public Collection<TypeMetadata> getRegisteredMetadata() {
        return metadataByType.values();
    }

    @Override
    public EntityTypeMetadata<EntityMetadata> getEntityMetadata(Class<?> entityType) {
        return getMetadata(entityType, EntityTypeMetadata.class);
    }

    @Override
    public RelationTypeMetadata<RelationMetadata> getRelationMetadata(Class<?> relationType) {
        return getMetadata(relationType, RelationTypeMetadata.class);
    }

    @Override
    public RelationTypeMetadata.Direction getRelationDirection(Set<Class<?>> sourceTypes, RelationTypeMetadata<RelationMetadata> relationMetadata, Set<Class<?>> targetTypes) {
        Class<?> outgoingType = outgoingTypes.get(relationMetadata);
        Class<?> incomingType = incomingTypes.get(relationMetadata);
        if (sourceTypes.contains(outgoingType) && targetTypes.contains(incomingType)) {
            return RelationTypeMetadata.Direction.OUTGOING;
        }
        if (targetTypes.contains(outgoingType) && sourceTypes.contains(incomingType)) {
            return RelationTypeMetadata.Direction.INCOMING;
        }
        throw new CdoException("The relation '" + relationMetadata + "' is not defined for the instances.");
    }

    private boolean isEntityType(AnnotatedType annotatedType) {
        return isOfDefinitionType(annotatedType, EntityDefinition.class);
    }

    private boolean isRelationType(AnnotatedType annotatedType) {
        return isOfDefinitionType(annotatedType, RelationDefinition.class);
    }

    private boolean isOfDefinitionType(AnnotatedType annotatedType, Class<? extends Annotation> definitionType) {
        Annotation definition = annotatedType.getByMetaAnnotation(definitionType);
        if (definition != null) {
            return true;
        }
        for (Class<?> superType : annotatedType.getAnnotatedElement().getInterfaces()) {
            if (isOfDefinitionType(new AnnotatedType(superType), definitionType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a {@link EntityTypeMetadata} instance for the given {@link AnnotatedType}.
     *
     * @param annotatedType        The {@link AnnotatedType}.
     * @param superTypes           The metadata collection of the super types.
     * @param methodMetadataOfType The method metadata of the type.
     * @return The {@link EntityTypeMetadata} instance representing the annotated type.
     */
    private EntityTypeMetadata<EntityMetadata> createEntityTypeMetadata(AnnotatedType annotatedType, List<TypeMetadata> superTypes,
                                                                        Collection<MethodMetadata> methodMetadataOfType) {
        IndexedPropertyMethodMetadata indexedProperty = getIndexedPropertyMethodMetadata(methodMetadataOfType);
        DatastoreEntityMetadata<EntityDiscriminator> datastoreEntityMetadata = metadataFactory.createEntityMetadata(annotatedType, metadataByType);
        EntityTypeMetadata entityTypeMetadata = new EntityTypeMetadata(annotatedType, superTypes, methodMetadataOfType, indexedProperty, datastoreEntityMetadata);
        return entityTypeMetadata;
    }

    /**
     * Get or create a {@link RelationTypeMetadata} instance for the given {@link AnnotatedType}.
     *
     * @param annotatedType        The {@link AnnotatedType}.
     * @param superTypes           The metadata collection of the super types.
     * @param methodMetadataOfType The method metadata of the type.
     * @return The {@link RelationTypeMetadata} instance representing the annotated type.
     */
    private RelationTypeMetadata<RelationMetadata> createRelationTypeMetadata(AnnotatedType annotatedType, List<TypeMetadata> superTypes,
                                                                              Collection<MethodMetadata> methodMetadataOfType) {
        RelationTypeMetadata relationTypeMetadata = (RelationTypeMetadata) metadataByType.get(annotatedType.getAnnotatedElement());
        if (relationTypeMetadata == null) {
            DatastoreRelationMetadata<RelationDiscriminator> datastoreRelationMetadata = metadataFactory.createRelationMetadata(annotatedType, metadataByType);
            relationTypeMetadata = new RelationTypeMetadata(annotatedType, superTypes, methodMetadataOfType, datastoreRelationMetadata);
            metadataByType.put(annotatedType.getAnnotatedElement(), relationTypeMetadata);
        }
        return relationTypeMetadata;
    }

    private List<TypeMetadata> getSuperTypeMetadata(AnnotatedType annotatedType) {
        List<TypeMetadata> superTypes = new ArrayList<>();
        for (Class<?> i : annotatedType.getAnnotatedElement().getInterfaces()) {
            superTypes.add(getOrCreateTypeMetadata(i));
        }
        return superTypes;
    }

    /**
     * Determine the indexed property from a list of method metadata.
     *
     * @param methodMetadataOfType The list of method metadata.
     * @return The {@link IndexedPropertyMethodMetadata}.
     */
    private IndexedPropertyMethodMetadata getIndexedPropertyMethodMetadata(Collection<MethodMetadata> methodMetadataOfType) {
        IndexedPropertyMethodMetadata indexedProperty = null;
        for (MethodMetadata methodMetadata : methodMetadataOfType) {
            AnnotatedMethod annotatedMethod = methodMetadata.getAnnotatedMethod();
            Annotation indexedAnnotation = annotatedMethod.getByMetaAnnotation(IndexDefinition.class);
            if (indexedAnnotation != null) {
                if (!(methodMetadata instanceof PrimitivePropertyMethodMetadata)) {
                    throw new CdoException("Only primitive properties are allowed to be used for indexing.");
                }
                indexedProperty = new IndexedPropertyMethodMetadata((PropertyMethod) annotatedMethod, (PrimitivePropertyMethodMetadata) methodMetadata, metadataFactory.createIndexedPropertyMetadata((PropertyMethod) annotatedMethod));
            }
        }
        return indexedProperty;
    }

    /**
     * Return the collection of method metadata from the given collection of annotateed methods.
     *
     * @param annotatedMethods The collection of annotated methods.
     * @return The collection of method metadata.
     */
    private Collection<MethodMetadata> getMethodMetadataOfType(AnnotatedType annotatedType, Collection<AnnotatedMethod> annotatedMethods) {
        Collection<MethodMetadata> methodMetadataOfType = new ArrayList<>();
        // Collect the getter methods as they provide annotations holding meta information also to be applied to setters
        for (AnnotatedMethod annotatedMethod : annotatedMethods) {
            MethodMetadata methodMetadata;
            ResultOf resultOf = annotatedMethod.getAnnotation(ResultOf.class);
            ImplementedBy implementedBy = annotatedMethod.getAnnotation(ImplementedBy.class);
            if (implementedBy != null) {
                methodMetadata = new ImplementedByMethodMetadata(annotatedMethod, implementedBy.value(), metadataFactory.createImplementedByMetadata(annotatedMethod));
            } else if (resultOf != null) {
                methodMetadata = createResultOfMetadata(annotatedMethod, resultOf);
            } else if (annotatedMethod instanceof PropertyMethod) {
                methodMetadata = createPropertyMethodMetadata(annotatedType, (PropertyMethod) annotatedMethod);
            } else {
                methodMetadata = new UnsupportedOperationMethodMetadata((UserMethod) annotatedMethod);
            }
            methodMetadataOfType.add(methodMetadata);
        }
        return methodMetadataOfType;
    }

    private MethodMetadata createPropertyMethodMetadata(AnnotatedType annotatedType, PropertyMethod propertyMethod) {
        MethodMetadata methodMetadata;
        Class<?> propertyType = propertyMethod.getType();
        if (Collection.class.isAssignableFrom(propertyType)) {
            RelationTypeMetadata.Direction relationDirection = metadataFactory.getRelationDirection(propertyMethod);
            RelationTypeMetadata relationshipType = new RelationTypeMetadata(metadataFactory.createRelationMetadata(propertyMethod, metadataByType));
            methodMetadata = new CollectionPropertyMethodMetadata(propertyMethod, relationshipType, relationDirection, metadataFactory.createCollectionPropertyMetadata(propertyMethod));
        } else if (annotatedMethods.containsKey(propertyType)) {
            RelationTypeMetadata.Direction relationDirection = metadataFactory.getRelationDirection(propertyMethod);
            RelationTypeMetadata relationMetadata;
            if (isEntityType(new AnnotatedType(propertyType))) {
                relationMetadata = new RelationTypeMetadata(metadataFactory.createRelationMetadata(propertyMethod, metadataByType));
            } else if (isRelationType(new AnnotatedType(propertyType))) {
                TypeMetadata propertyTypeMetadata = getOrCreateTypeMetadata(propertyType);
                relationMetadata = (RelationTypeMetadata) propertyTypeMetadata;
            } else {
                throw new CdoException("Unsupported type for reference property: " + propertyType.getName());
            }
            methodMetadata = new ReferencePropertyMethodMetadata(propertyMethod, relationMetadata, relationDirection, metadataFactory.createReferencePropertyMetadata(propertyMethod));
            switch (relationDirection) {
                case OUTGOING:
                    outgoingTypes.put(relationMetadata, annotatedType.getAnnotatedElement());
                    break;
                case INCOMING:
                    incomingTypes.put(relationMetadata, annotatedType.getAnnotatedElement());
                    break;
            }
        } else {
            if (Enum.class.isAssignableFrom(propertyType)) {
                methodMetadata = new EnumPropertyMethodMetadata(propertyMethod, propertyType, metadataFactory.createEnumPropertyMetadata(propertyMethod));
            } else {
                methodMetadata = new PrimitivePropertyMethodMetadata(propertyMethod, metadataFactory.createPrimitivePropertyMetadata(propertyMethod));
            }
        }
        return methodMetadata;
    }

    private MethodMetadata createResultOfMetadata(AnnotatedMethod annotatedMethod, ResultOf resultOf) {
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

    private <T extends TypeMetadata> T getMetadata(Class<?> type, Class<T> metadataType) {
        TypeMetadata typeMetadata = metadataByType.get(type);
        if (typeMetadata == null) {
            throw new CdoException("Cannot resolve metadata for type " + type.getName() + ".");
        }
        if (!metadataType.isAssignableFrom(typeMetadata.getClass())) {
            throw new CdoException("Expected metadata of type '" + metadataType.getName() + "' but got '" + typeMetadata.getClass() + "' for type '" + type + "'");
        }
        return metadataType.cast(typeMetadata);
    }
}
