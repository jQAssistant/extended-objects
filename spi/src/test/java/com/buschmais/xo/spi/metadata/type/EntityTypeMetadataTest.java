package com.buschmais.xo.spi.metadata.type;

import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.reflection.AnnotatedType;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EntityTypeMetadataTest {

    EntityTypeMetadata<DatastoreEntity> a1 = new EntityTypeMetadata<>(new AnnotatedType(A.class)
        , emptyList(), emptyList(), false, false, null, new DatastoreEntity("A"));
    EntityTypeMetadata<DatastoreEntity> a2 = new EntityTypeMetadata<>(new AnnotatedType(A.class)
        , emptyList(), emptyList(), false, false, null, new DatastoreEntity("A"));
    EntityTypeMetadata<DatastoreEntity> b = new EntityTypeMetadata<>(new AnnotatedType(B.class)
        , emptyList(), emptyList(), false, false, null, new DatastoreEntity("B"));

    @Test
    public void hashCodeEquals() {
        assertThat(a1.hashCode(), equalTo(a2.hashCode()));
        assertThat(a1.equals(a2), equalTo(true));
        assertThat(a1.equals(b), equalTo(false));
    }

    @Test
    public void metadataSet() {
        TypeMetadataSet<DatastoreTypeMetadata<?>> set1 = new TypeMetadataSet<>();
        set1.add(a1);
        TypeMetadataSet<DatastoreTypeMetadata<?>> set2 = new TypeMetadataSet<>();
        set2.add(a1);
        TypeMetadataSet<DatastoreTypeMetadata<?>> set3 = new TypeMetadataSet<>();
        set3.add(b);
        assertThat(set3.equals(set1), equalTo(false));
        TypeMetadataSet<DatastoreTypeMetadata<?>> set4 = new TypeMetadataSet<>();
        set4.add(a1);
        set4.add(a2);
        TypeMetadataSet<DatastoreTypeMetadata<?>> set5 = new TypeMetadataSet<>();
        set5.add(a1);
        set5.add(a2);
        assertThat(set4.hashCode(), equalTo(set5.hashCode()));
        assertThat(set4.equals(set5), equalTo(true));
    }
}
