package com.buschmais.xo.impl.test.bootstrap.provider;

import java.util.List;
import java.util.Map;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.test.bootstrap.provider.metadata.TestEntityMetadata;
import com.buschmais.xo.impl.test.bootstrap.provider.metadata.TestRelationMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedElement;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;
import com.buschmais.xo.spi.reflection.AnnotatedType;
import com.buschmais.xo.spi.reflection.PropertyMethod;

public class TestXODatastore<D extends DatastoreSession> implements Datastore<D, TestEntityMetadata, String, TestRelationMetadata, String> {

    private final XOUnit xoUnit;

    public TestXODatastore(XOUnit xoUnit) {
        this.xoUnit = xoUnit;
    }

    @Override
    public DatastoreMetadataFactory<TestEntityMetadata, String, TestRelationMetadata, String> getMetadataFactory() {
        return new DatastoreMetadataFactory<TestEntityMetadata, String, TestRelationMetadata, String>() {
            @Override
            public TestEntityMetadata createEntityMetadata(AnnotatedType annotatedType, List<TypeMetadata> superTypes, Map<Class<?>, TypeMetadata> metadataByType) {
                return new TestEntityMetadata(annotatedType.getAnnotatedElement().getName());
            }

            @Override
            public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(AnnotatedMethod annotatedMethod) {
                return null;
            }

            @Override
            public <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPropertyMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public <IndexedPropertyMetadata> IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public TestRelationMetadata createRelationMetadata(AnnotatedElement<?> annotatedElement, Map<Class<?>, TypeMetadata> metadataByType) {
                return null;
            }
        };
    }

    @Override
    public D createSession() {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public void init(Map<Class<?>, TypeMetadata> registeredMetadata) {
    }

    public XOUnit getXOUnit() {
        return xoUnit;
    }
}
