package com.buschmais.cdo.impl.test.bootstrap.provider;

import com.buschmais.cdo.spi.bootstrap.CdoUnit;
import com.buschmais.cdo.spi.datastore.*;
import com.buschmais.cdo.spi.metadata.MetadataProvider;
import com.buschmais.cdo.spi.metadata.RelationMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.reflection.BeanMethod;
import com.buschmais.cdo.spi.reflection.PropertyMethod;

import java.util.Collection;
import java.util.Map;

public class TestCdoDatastore<D extends DatastoreSession> implements Datastore<D> {

    private CdoUnit cdoUnit;

    public TestCdoDatastore(CdoUnit cdoUnit) {
        this.cdoUnit = cdoUnit;
    }

    @Override
    public DatastoreMetadataFactory<?> getMetadataFactory() {
        return new DatastoreMetadataFactory<Object>() {
            @Override
            public Object createEntityMetadata(Class<?> type, Map<Class<?>, TypeMetadata> metadataByType) {
                return null;
            }

            @Override
            public <ImplementedByMetadata> ImplementedByMetadata createImplementedByMetadata(BeanMethod beanMethod) {
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
    public DatastoreMetadataProvider createMetadataProvider(Collection<TypeMetadata> entityTypes) {
        return new DatastoreMetadataProvider() {
            @Override
            public TypeSet getTypes(Object o) {
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
