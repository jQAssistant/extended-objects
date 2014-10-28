package com.buschmais.xo.impl.metadata;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.annotation.*;
import com.buschmais.xo.impl.MetadataProvider;
import com.buschmais.xo.spi.annotation.EntityDefinition;
import com.buschmais.xo.spi.annotation.IndexDefinition;
import com.buschmais.xo.spi.annotation.QueryDefinition;
import com.buschmais.xo.spi.annotation.RelationDefinition;
import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.metadata.method.*;
import com.buschmais.xo.spi.metadata.type.*;
import com.buschmais.xo.spi.reflection.*;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.annotation.ResultOf.Parameter;
import static com.buschmais.xo.spi.annotation.RelationDefinition.FromDefinition;
import static com.buschmais.xo.spi.annotation.RelationDefinition.ToDefinition;
import static com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction;

/**
 * Implementation of the {@link MetadataProvider}.
 *
 * @param <EntityMetadata>
 *            The type of datastore specific entity metadata.
 * @param <EntityDiscriminator>
 *            The type of datastore specific entity type discriminators.
 * @param <RelationMetadata>
 *            The type of datastore specific relation metadata.
 * @param <RelationDiscriminator>
 *            The type of datastore specific relationtype discriminators.
 */
public class MetadataProviderImpl<EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator>
        implements MetadataProvider<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeMetadata.class);

    private final DatastoreMetadataFactory<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> metadataFactory;
    private final EntityTypeMetadataResolver<EntityMetadata, EntityDiscriminator> entityTypeMetadataResolver;
    private final RelationTypeMetadataResolver<EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> relationTypeMetadataResolver;
    private final Map<Class<?>, Collection<AnnotatedMethod>> annotatedMethods;
    private final Map<Class<?>, TypeMetadata> metadataByType = new LinkedHashMap<>();
    private final Cache<AnnotatedElement, com.google.common.base.Optional<Annotation>> queryTypes = CacheBuilder.newBuilder().build();

    /**
     * Constructor.
     *
     * @param types
     *            All classes as provided by the XO unit.
     * @param datastore
     *            The datastore.
     */
    public MetadataProviderImpl(Collection<Class<?>> types, Datastore<?, EntityMetadata, EntityDiscriminator, RelationMetadata, RelationDiscriminator> datastore) {
        this.metadataFactory = datastore.getMetadataFactory();
        DependencyResolver.DependencyProvider<Class<?>> classDependencyProvider = dependent -> new HashSet<>(Arrays.asList(dependent.getInterfaces()));
        List<Class<?>> allClasses = DependencyResolver.newInstance(types, classDependencyProvider).resolve();
        LOGGER.debug("Processing types {}", allClasses);
        this.annotatedMethods = new HashMap<>();
        for (Class<?> currentClass : allClasses) {
            if (!currentClass.isInterface()) {
                throw new XOException("Type " + currentClass.getName() + " is not an interface.");
            }
            annotatedMethods.put(currentClass, BeanMethodProvider.newInstance(currentClass).getMethods());
        }
        for (Class<?> currentClass : allClasses) {
            getOrCreateTypeMetadata(currentClass);
        }
        entityTypeMetadataResolver = new EntityTypeMetadataResolver<>(metadataByType);
        relationTypeMetadataResolver = new RelationTypeMetadataResolver<>(metadataByType);
        metadataByType.put(CompositeObject.class, new SimpleTypeMetadata(new AnnotatedType(CompositeObject.class), Collections.<TypeMetadata> emptyList(),
                Collections.<MethodMetadata<?, ?>> emptyList(), null));
    }

    @Override
    public TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> getTypes(Set<EntityDiscriminator> entityDiscriminators) {
        return entityTypeMetadataResolver.getTypes(entityDiscriminators);
    }

    @Override
    public TypeMetadataSet<RelationTypeMetadata<RelationMetadata>> getRelationTypes(Set<EntityDiscriminator> sourceDiscriminators,
            RelationDiscriminator discriminator, Set<EntityDiscriminator> targetDiscriminators) {
        return relationTypeMetadataResolver.getRelationTypes(sourceDiscriminators, discriminator, targetDiscriminators);
    }

    @Override
    public Set<EntityDiscriminator> getEntityDiscriminators(TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types) {
        Set<EntityDiscriminator> entityDiscriminators = new HashSet<>();
        for (EntityTypeMetadata<EntityMetadata> entityTypeMetadata : types) {
            Set<EntityDiscriminator> discriminatorsOfType = entityTypeMetadataResolver.getDiscriminators(entityTypeMetadata);
            entityDiscriminators.addAll(discriminatorsOfType);
        }
        return entityDiscriminators;
    }

    @Override
    public Map<Class<?>, TypeMetadata> getRegisteredMetadata() {
        return Collections.unmodifiableMap(metadataByType);
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
    public Direction getRelationDirection(Set<Class<?>> sourceTypes, RelationTypeMetadata<RelationMetadata> relationMetadata, Set<Class<?>> targetTypes) {
        if (sourceTypes.contains(relationMetadata.getFromType()) && targetTypes.contains(relationMetadata.getToType())) {
            return Direction.FROM;
        } else if (targetTypes.contains(relationMetadata.getFromType()) && sourceTypes.contains(relationMetadata.getToType())) {
            return Direction.TO;
        }
        throw new XOException("The relation '" + relationMetadata + "' is not defined for the instances.");
    }

    @Override
    public RepositoryTypeMetadata getRepositoryMetadata(Class<?> repositoryType) {
        return getMetadata(repositoryType, RepositoryTypeMetadata.class);
    }

    @Override
    public <R> AbstractRelationPropertyMethodMetadata<?> getPropertyMetadata(Class<?> entityType, Class<R> relationType, Direction direction) {
        return relationTypeMetadataResolver.getRelationPropertyMethodMetadata(entityType, getRelationMetadata(relationType), direction);
    }

    /**
     * Return the {@link TypeMetadata} for a given type.
     * <p>
     * The metadata will be created if it does not exist yet.
     * </p>
     *
     * @param type
     *            The type.
     * @return The {@link TypeMetadata}.
     */
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

    /**
     * Create the {@link TypeMetadata} for the given {@link AnnotatedType}.
     *
     * @param annotatedType
     *            The {@link AnnotatedType}.
     * @return The corresponding metadata.
     */
    private TypeMetadata createTypeMetadata(AnnotatedType annotatedType) {
        Class<?> currentClass = annotatedType.getAnnotatedElement();
        Collection<AnnotatedMethod> annotatedMethods = this.annotatedMethods.get(currentClass);
        if (annotatedMethods == null) {
            throw new XOException("XO unit does not declare '" + currentClass.getName() + "'.");
        }
        Collection<MethodMetadata<?, ?>> methodMetadataOfType = getMethodMetadataOfType(annotatedType, annotatedMethods);
        TypeMetadata metadata;
        List<TypeMetadata> superTypes = getSuperTypeMetadata(annotatedType);
        if (isEntityType(annotatedType)) {
            metadata = createEntityTypeMetadata(annotatedType, superTypes, methodMetadataOfType);
        } else if (isRelationType(annotatedType)) {
            metadata = createRelationTypeMetadata(annotatedType, superTypes, methodMetadataOfType);
        } else if (annotatedType.isAnnotationPresent(Repository.class)) {
            metadata = createRepositoryTypeMetadata(annotatedType, superTypes, methodMetadataOfType);
        } else {
            IndexedPropertyMethodMetadata indexedProperty = getIndexedPropertyMethodMetadata(methodMetadataOfType);
            metadata = new SimpleTypeMetadata(annotatedType, superTypes, methodMetadataOfType, indexedProperty);
        }
        return metadata;
    }

    /**
     * Determines if an {@link AnnotatedType} represents an entity type.
     *
     * @param annotatedType
     *            The {@link AnnotatedType}.
     * @return <code>true</code> if the annotated type represents an entity
     *         type.
     */
    private boolean isEntityType(AnnotatedType annotatedType) {
        return isOfDefinitionType(annotatedType, EntityDefinition.class);
    }

    /**
     * Determines if an {@link AnnotatedType} represents a relation type.
     *
     * @param annotatedType
     *            The {@link AnnotatedType}.
     * @return <code>true</code> if the annotated type represents relation type.
     */
    private boolean isRelationType(AnnotatedType annotatedType) {
        return isOfDefinitionType(annotatedType, RelationDefinition.class);
    }

    /**
     * Determines if an {@link AnnotatedType} represents a specific type
     * identified by a meta annotation.
     *
     * @param annotatedType
     *            The {@link AnnotatedType}.
     * @param definitionType
     *            The meta annotation.
     * @return <code>true</code> if the annotated type represents relation type.
     */
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
     * Create a {@link EntityTypeMetadata} instance for the given
     * {@link AnnotatedType}.
     *
     * @param annotatedType
     *            The {@link AnnotatedType}.
     * @param superTypes
     *            The metadata collection of the super types.
     * @param methodMetadataOfType
     *            The method metadata of the type.
     * @return The {@link EntityTypeMetadata} instance representing the
     *         annotated type.
     */
    private EntityTypeMetadata<EntityMetadata> createEntityTypeMetadata(AnnotatedType annotatedType, List<TypeMetadata> superTypes,
            Collection<MethodMetadata<?, ?>> methodMetadataOfType) {
        IndexedPropertyMethodMetadata indexedProperty = getIndexedPropertyMethodMetadata(methodMetadataOfType);
        EntityMetadata datastoreEntityMetadata = metadataFactory.createEntityMetadata(annotatedType, metadataByType);
        boolean abstractType = annotatedType.isAnnotationPresent(Abstract.class);
        boolean finalType = annotatedType.isAnnotationPresent(Abstract.class);
        return new EntityTypeMetadata<>(annotatedType, superTypes, methodMetadataOfType, abstractType, finalType, indexedProperty, datastoreEntityMetadata);
    }

    /**
     * Get or create a {@link RelationTypeMetadata} instance for the given
     * {@link AnnotatedType}.
     *
     * @param annotatedType
     *            The {@link AnnotatedType}.
     * @param superTypes
     *            The metadata collection of the super types.
     * @param methodMetadataOfType
     *            The method metadata of the type.
     * @return The {@link RelationTypeMetadata} instance representing the
     *         annotated type.
     */
    private RelationTypeMetadata<RelationMetadata> createRelationTypeMetadata(AnnotatedType annotatedType, List<TypeMetadata> superTypes,
            Collection<MethodMetadata<?, ?>> methodMetadataOfType) {
        Class<?> fromType = null;
        Class<?> toType = null;
        Collection<MethodMetadata<?, ?>> current = methodMetadataOfType;
        Queue<TypeMetadata> queue = new LinkedList<>(superTypes);
        // Starting from the type to be created search all its properties and
        // those of its super types for reference properties defining the from
        // and to entity types
        do {
            for (MethodMetadata<?, ?> methodMetadata : current) {
                if (methodMetadata instanceof EntityReferencePropertyMethodMetadata) {
                    EntityReferencePropertyMethodMetadata<?> propertyMethodMetadata = (EntityReferencePropertyMethodMetadata<?>) methodMetadata;
                    Class<?> type = propertyMethodMetadata.getAnnotatedMethod().getType();
                    switch (propertyMethodMetadata.getDirection()) {
                    case FROM:
                        fromType = type;
                        break;
                    case TO:
                        toType = type;
                        break;
                    default:
                        throw propertyMethodMetadata.getDirection().createNotSupportedException();
                    }
                }
            }
            TypeMetadata superType = queue.poll();
            if (superType != null) {
                queue.addAll(superType.getSuperTypes());
                current = superType.getProperties();
            } else {
                current = null;
            }
        } while (current != null && fromType == null && toType == null);
        if (fromType == null || toType == null) {
            throw new XOException("Relation type '" + annotatedType.getAnnotatedElement().getName()
                    + "' does not define target entity properties for both directions.");
        }
        RelationMetadata relationMetadata = metadataFactory.createRelationMetadata(annotatedType, metadataByType);
        RelationTypeMetadata<RelationMetadata> relationTypeMetadata = new RelationTypeMetadata<>(annotatedType, superTypes, methodMetadataOfType, fromType,
                toType, relationMetadata);
        metadataByType.put(annotatedType.getAnnotatedElement(), relationTypeMetadata);
        return relationTypeMetadata;
    }

    private RepositoryTypeMetadata createRepositoryTypeMetadata(AnnotatedType annotatedType, List<TypeMetadata> superTypes,
            Collection<MethodMetadata<?, ?>> methodMetadataOfType) {
        RepositoryTypeMetadata repositoryTypeMetadata = new RepositoryTypeMetadata(annotatedType, superTypes, methodMetadataOfType);
        metadataByType.put(annotatedType.getAnnotatedElement(), repositoryTypeMetadata);
        return repositoryTypeMetadata;
    }

    /**
     * Returns a list of {@link TypeMetadata} representing the super types of
     * the given annotated type.
     *
     * @param annotatedType
     *            The {@link AnnotatedType}.
     * @return The list of {@link TypeMetadata} representing the super types.
     */
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
     * @param methodMetadataOfType
     *            The list of method metadata.
     * @return The {@link IndexedPropertyMethodMetadata}.
     */
    private IndexedPropertyMethodMetadata<?> getIndexedPropertyMethodMetadata(Collection<MethodMetadata<?, ?>> methodMetadataOfType) {
        for (MethodMetadata methodMetadata : methodMetadataOfType) {
            AnnotatedMethod annotatedMethod = methodMetadata.getAnnotatedMethod();
            Annotation indexedAnnotation = annotatedMethod.getByMetaAnnotation(IndexDefinition.class);
            if (indexedAnnotation != null) {
                if (!(methodMetadata instanceof PrimitivePropertyMethodMetadata)) {
                    throw new XOException("Only primitive properties are allowed to be used for indexing.");
                }
                return new IndexedPropertyMethodMetadata<>((PropertyMethod) annotatedMethod, (PrimitivePropertyMethodMetadata) methodMetadata,
                        metadataFactory.createIndexedPropertyMetadata((PropertyMethod) annotatedMethod));
            }
        }
        return null;
    }

    /**
     * Return the collection of method metadata from the given collection of
     * annotated methods.
     *
     * @param annotatedMethods
     *            The collection of annotated methods.
     * @return The collection of method metadata.
     */
    private Collection<MethodMetadata<?, ?>> getMethodMetadataOfType(AnnotatedType annotatedType, Collection<AnnotatedMethod> annotatedMethods) {
        Collection<MethodMetadata<?, ?>> methodMetadataOfType = new ArrayList<>();
        // Collect the getter methods as they provide annotations holding meta
        // information also to be applied to setters
        for (AnnotatedMethod annotatedMethod : annotatedMethods) {
            MethodMetadata<?, ?> methodMetadata;
            ImplementedBy implementedBy = annotatedMethod.getAnnotation(ImplementedBy.class);
            ResultOf resultOf = annotatedMethod.getAnnotation(ResultOf.class);
            if (implementedBy != null) {
                methodMetadata = new ImplementedByMethodMetadata<>(annotatedMethod, implementedBy.value(),
                        metadataFactory.createImplementedByMetadata(annotatedMethod));
            } else if (resultOf != null) {
                methodMetadata = createResultOfMetadata(annotatedMethod, resultOf);
            } else if (annotatedMethod instanceof PropertyMethod) {
                PropertyMethod propertyMethod = (PropertyMethod) annotatedMethod;
                Transient transientAnnotation = propertyMethod.getAnnotationOfProperty(Transient.class);
                if (transientAnnotation != null) {
                    methodMetadata = new TransientPropertyMethodMetadata(propertyMethod);
                } else {
                    methodMetadata = createPropertyMethodMetadata(annotatedType, propertyMethod);
                }
            } else {
                methodMetadata = new UnsupportedOperationMethodMetadata((UserMethod) annotatedMethod);
            }
            methodMetadataOfType.add(methodMetadata);
        }
        return methodMetadataOfType;
    }

    /**
     * Create the {@link MethodMetadata} for a property method.
     *
     * @param annotatedType
     *            The annotated type containing the property.
     * @param propertyMethod
     *            The property method.
     * @return The {@link MethodMetadata}.
     */
    private MethodMetadata<?, ?> createPropertyMethodMetadata(AnnotatedType annotatedType, PropertyMethod propertyMethod) {
        MethodMetadata<?, ?> methodMetadata;
        Class<?> propertyType = propertyMethod.getType();
        if (Collection.class.isAssignableFrom(propertyType)) {
            Type genericType = propertyMethod.getGenericType();
            ParameterizedType type = (ParameterizedType) genericType;
            Class<?> elementType;
            Type typeArgument = type.getActualTypeArguments()[0];
            if (typeArgument instanceof Class) {
                elementType = (Class<?>) typeArgument;
            } else if (typeArgument instanceof ParameterizedType) {
                ParameterizedType parameterizedTypeArgument = (ParameterizedType) typeArgument;
                elementType = (Class<?>) parameterizedTypeArgument.getRawType();
            } else {
                throw new XOException("Cannot determine argument type of collection property " + propertyMethod.getAnnotatedElement().toGenericString());
            }
            AnnotatedType annotatedTypeArgument = new AnnotatedType(elementType);
            if (isEntityType(annotatedTypeArgument)) {
                Direction relationDirection = getRelationDirection(propertyMethod, Direction.FROM);
                com.buschmais.xo.spi.reflection.AnnotatedElement<?> relationElement = getRelationDefinitionElement(propertyMethod);
                RelationTypeMetadata relationshipType = new RelationTypeMetadata<>(metadataFactory.createRelationMetadata(relationElement, metadataByType));
                methodMetadata = new EntityCollectionPropertyMethodMetadata<>(propertyMethod, relationshipType, relationDirection,
                        metadataFactory.createCollectionPropertyMetadata(propertyMethod));
            } else if (isRelationType(annotatedTypeArgument)) {
                TypeMetadata relationTypeMetadata = getOrCreateTypeMetadata(elementType);
                RelationTypeMetadata<RelationMetadata> relationMetadata = (RelationTypeMetadata) relationTypeMetadata;
                Direction relationDirection = getRelationDirection(annotatedType, propertyMethod, relationTypeMetadata, relationMetadata);
                methodMetadata = new RelationCollectionPropertyMethodMetadata<>(propertyMethod, relationMetadata, relationDirection,
                        metadataFactory.createCollectionPropertyMetadata(propertyMethod));
            } else {
                throw new XOException("Unsupported type argument '" + elementType.getName() + "' for collection property: " + propertyType.getName());
            }
        } else if (annotatedMethods.containsKey(propertyType)) {
            AnnotatedType referencedType = new AnnotatedType(propertyType);
            Direction relationDirection;
            RelationTypeMetadata<RelationMetadata> relationMetadata;
            if (isEntityType(referencedType)) {
                relationDirection = getRelationDirection(propertyMethod, Direction.FROM);
                com.buschmais.xo.spi.reflection.AnnotatedElement<?> relationElement = getRelationDefinitionElement(propertyMethod);
                relationMetadata = new RelationTypeMetadata<>(metadataFactory.createRelationMetadata(relationElement, metadataByType));
                methodMetadata = new EntityReferencePropertyMethodMetadata<>(propertyMethod, relationMetadata, relationDirection,
                        metadataFactory.createReferencePropertyMetadata(propertyMethod));
            } else if (isRelationType(referencedType)) {
                TypeMetadata relationTypeMetadata = getOrCreateTypeMetadata(propertyType);
                relationMetadata = (RelationTypeMetadata) relationTypeMetadata;
                relationDirection = getRelationDirection(annotatedType, propertyMethod, relationTypeMetadata, relationMetadata);
                methodMetadata = new RelationReferencePropertyMethodMetadata<>(propertyMethod, relationMetadata, relationDirection,
                        metadataFactory.createReferencePropertyMetadata(propertyMethod));
            } else {
                throw new XOException("Unsupported type for reference property: " + propertyType.getName());
            }
        } else {
            methodMetadata = new PrimitivePropertyMethodMetadata<>(propertyMethod, metadataFactory.createPropertyMetadata(propertyMethod));
        }
        return methodMetadata;
    }

    private Direction getRelationDirection(PropertyMethod propertyMethod, Direction defaultDirection) {
        Annotation fromAnnotation = propertyMethod.getByMetaAnnotationOfProperty(FromDefinition.class);
        Annotation toAnnotation = propertyMethod.getByMetaAnnotationOfProperty(ToDefinition.class);
        if (fromAnnotation != null && toAnnotation != null) {
            throw new XOException("The relation property '" + propertyMethod.getName() + "' must not specifiy both directions.'");
        }
        Direction direction = null;
        if (fromAnnotation != null) {
            direction = RelationTypeMetadata.Direction.FROM;
        }
        if (toAnnotation != null) {
            direction = RelationTypeMetadata.Direction.TO;
        }
        return direction != null ? direction : defaultDirection;
    }

    private Direction getRelationDirection(AnnotatedType annotatedEntityType, PropertyMethod propertyMethod, TypeMetadata propertyTypeMetadata,
            RelationTypeMetadata<RelationMetadata> relationMetadata) {
        Direction relationDirection = getRelationDirection(propertyMethod, null);
        if (relationDirection == null) {
            Class<?> fromType = relationMetadata.getFromType();
            Class<?> toType = relationMetadata.getToType();
            if (fromType.equals(toType)) {
                throw new XOException("Direction of property '" + propertyMethod.getAnnotatedElement().toGenericString()
                        + "' is ambiguous and must be specified.");
            }
            if (annotatedEntityType.getAnnotatedElement().equals(fromType)) {
                relationDirection = Direction.FROM;
            } else if (annotatedEntityType.getAnnotatedElement().equals(toType)) {
                relationDirection = Direction.TO;
            } else {
                throw new XOException("Cannot determine relation direction for type '" + propertyTypeMetadata.getAnnotatedType().getName() + "'");
            }
        }
        return relationDirection;
    }

    /**
     * Determines the {@link AnnotatedElement} which is annotated with
     * {@link RelationDefinition}.
     *
     * @param propertyMethod
     *            The property method to start with.
     * @return The annotated element.
     */
    private com.buschmais.xo.spi.reflection.AnnotatedElement<?> getRelationDefinitionElement(PropertyMethod propertyMethod) {
        if (propertyMethod.getByMetaAnnotationOfProperty(RelationDefinition.class) != null) {
            return propertyMethod;
        }
        Annotation[] declaredAnnotations = propertyMethod.getAnnotationsOfProperty();
        for (int i = 0; i < declaredAnnotations.length; i++) {
            com.buschmais.xo.spi.reflection.AnnotatedElement<?> annotationTypeElement = new AnnotatedType(declaredAnnotations[i].annotationType());
            if (annotationTypeElement.getByMetaAnnotation(RelationDefinition.class) != null) {
                return annotationTypeElement;
            }
        }
        return propertyMethod;
    }

    /**
     * Creates the method metadata for methods annotated with {@link ResultOf}.
     *
     * @param annotatedMethod
     *            The annotated method-
     * @param resultOf
     *            The {@link com.buschmais.xo.api.annotation.ResultOf}
     *            annotation.
     * @return The method metadata.
     */
    private MethodMetadata<?, ?> createResultOfMetadata(AnnotatedMethod annotatedMethod, ResultOf resultOf) {
        Method method = annotatedMethod.getAnnotatedElement();

        // Determine query type
        Class<?> methodReturnType = method.getReturnType();
        Class<?> returnType;
        if (Result.class.isAssignableFrom(methodReturnType)) {
            Type genericReturnType = method.getGenericReturnType();
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            returnType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        } else {
            returnType = methodReturnType;
        }

        // Determine query type
        AnnotatedElement query = resultOf.query();
        if (Object.class.equals(query)) {
            if (annotatedMethod.getByMetaAnnotation(QueryDefinition.class) != null) {
                query = annotatedMethod.getAnnotatedElement();
            } else {
                query = returnType;
            }
        }
        // Determine parameter bindings
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<Parameter> parameters = new ArrayList<>();
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            Parameter parameter = null;
            for (Annotation annotation : parameterAnnotation) {
                if (Parameter.class.equals(annotation.annotationType())) {
                    parameter = (Parameter) annotation;
                }
            }
            if (parameter == null) {
                throw new XOException("Cannot determine parameter names for '" + method.getName() + "', all parameters must be annotated with '"
                        + Parameter.class.getName() + "'.");
            }
            parameters.add(parameter);
        }
        boolean singleResult = !Iterable.class.isAssignableFrom(methodReturnType);
        return new ResultOfMethodMetadata<>(annotatedMethod, query, returnType, resultOf.usingThisAs(), parameters, singleResult);
    }

    /**
     * Return the {@link TypeMetadata} instance representing the given type.
     *
     * @param type
     *            The type.
     * @param metadataType
     *            The expected metadata type.
     * @param <T>
     *            The metadata type.
     * @return The {@link TypeMetadata} instance.
     */
    private <T extends TypeMetadata> T getMetadata(Class<?> type, Class<T> metadataType) {
        TypeMetadata typeMetadata = metadataByType.get(type);
        if (typeMetadata == null) {
            throw new XOException("Cannot resolve metadata for type " + type.getName() + ".");
        }
        if (!metadataType.isAssignableFrom(typeMetadata.getClass())) {
            throw new XOException("Expected metadata of type '" + metadataType.getName() + "' but got '" + typeMetadata.getClass() + "' for type '" + type
                    + "'");
        }
        return metadataType.cast(typeMetadata);
    }

    @Override
    public <QL extends Annotation> QL getQuery(AnnotatedElement annotatedElement) {
        Optional<Annotation> cachedOptional = queryTypes.getIfPresent(annotatedElement);
        if (cachedOptional == null) {
            AnnotatedQueryElement element = new AnnotatedQueryElement(annotatedElement);
            Annotation annotation = element.getByMetaAnnotation(QueryDefinition.class);
            cachedOptional = Optional.fromNullable(annotation);
            queryTypes.put(annotatedElement, cachedOptional);
        }
        return cachedOptional.isPresent() ? (QL) cachedOptional.get() : null;
    }

    /**
     * An annotated element.
     */
    private static class AnnotatedQueryElement extends AbstractAnnotatedElement<AnnotatedElement> {

        /**
         * Constructor.
         *
         * @param typeExpression
         *            The expression.
         */
        public AnnotatedQueryElement(AnnotatedElement typeExpression) {
            super(typeExpression);
        }

        @Override
        public String getName() {
            return toString();
        }
    }

}
