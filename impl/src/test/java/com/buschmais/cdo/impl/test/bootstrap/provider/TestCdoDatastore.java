package com.buschmais.cdo.impl.test.bootstrap.provider;

import com.buschmais.cdo.impl.test.bootstrap.provider.metadata.TestEntityMetadata;
import com.buschmais.cdo.spi.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.datastore.*;
import com.buschmais.cdo.spi.metadata.MetadataProvider;
import com.buschmais.cdo.spi.metadata.RelationMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.reflection.TypeMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;

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
            public TestEntityMetadata createEntityMetadata(Class<?> type, Map<Class<?>, TypeMetadata<TestEntityMetadata>> metadataByType) {
                return new TestEntityMetadata(type.getName());
            }

            @Override
            public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(TypeMethod typeMethod) {
                return null;
            }

            @Override
            public <CollectionPropertyMetadata> CollectionPropertyMetadata createCollectionPropertyMetadata(PropertyMethod beanPropertyMethod) {
                return null;
            }

            @Override
            public <ReferencePropertyMetadata> ReferencePropertyMetadata createReferencePropertyMetadata(PropertyMethod beanPropertyMethod) {
                return null;
            }

            @Override
            public <PrimitivePropertyMetadata> PrimitivePropertyMetadata createPrimitvePropertyMetadata(PropertyMethod beanPropertyMethod) {
                return null;
            }

            @Override
            public <EnumPropertyMetadata> EnumPropertyMetadata createEnumPropertyMetadata(PropertyMethod beanPropertyMethod) {
                return null;
            }

            @Override
            public <IndexedPropertyMetadata> IndexedPropertyMetadata createIndexedPropertyMetadata(PropertyMethod beanMethod) {
                return null;
            }

            @Override
            public <RelationMetadata> RelationMetadata createRelationMetadata(PropertyMethod beanPropertyMethod) {
                return null;
            }

            @Override
            public RelationMetadata.Direction getRelationDirection(PropertyMethod beanPropertyMethod) {
                return null;
            }
        };
    }

    @Override
    public D createSession(MetadataProvider metadataProvider) {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public void init(MetadataProvider metadataProvider) {
    }

    public CdoUnit getCdoUnit() {
        return cdoUnit;
    }
}
