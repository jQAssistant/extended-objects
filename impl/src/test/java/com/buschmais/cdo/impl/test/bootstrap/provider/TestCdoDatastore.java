package com.buschmais.cdo.impl.test.bootstrap.provider;

import com.buschmais.cdo.impl.test.bootstrap.provider.metadata.TestEntityMetadata;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.datastore.*;
import com.buschmais.cdo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;
import com.buschmais.cdo.spi.metadata.type.TypeMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;
import java.util.Map;

public class TestCdoDatastore<D extends DatastoreSession> implements Datastore<D, TestEntityMetadata, String> {

    private CdoUnit cdoUnit;

    public TestCdoDatastore(CdoUnit cdoUnit) {
        this.cdoUnit = cdoUnit;
    }

    @Override
    public DatastoreMetadataFactory<TestEntityMetadata, String> getMetadataFactory() {
        return new DatastoreMetadataFactory<TestEntityMetadata, String>() {
            @Override
            public TestEntityMetadata createEntityMetadata(AnnotatedType annotatedType, Map<Class<?>, TypeMetadata> metadataByType) {
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
            public <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPrimitivePropertyMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public <EnumPropertyMetadata> EnumPropertyMetadata createEnumPropertyMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public <IndexedPropertyMetadata> IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public <RelationMetadata> RelationMetadata createRelationMetadata(PropertyMethod propertyMethod) {
                return null;
            }

            @Override
            public RelationTypeMetadata.Direction getRelationDirection(PropertyMethod propertyMethod) {
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
    public void init(Collection<TypeMetadata> registeredMetadata) {
    }

    public CdoUnit getCdoUnit() {
        return cdoUnit;
    }
}
