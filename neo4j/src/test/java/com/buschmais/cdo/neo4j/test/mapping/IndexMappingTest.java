package com.buschmais.cdo.neo4j.test.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.mapping.composite.A;
import com.buschmais.cdo.neo4j.test.mapping.composite.D;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class IndexMappingTest extends AbstractCdoManagerTest {


    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class,D.class};
    }

    @Test
    public void indexedProperty() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a1 = cdoManager.create(A.class);
        a1.setIndex("1");
        A a2 = cdoManager.create(A.class);
        a2.setIndex("2");
        cdoManager.commit();
        cdoManager.begin();
        assertThat(cdoManager.find(A.class, "1").iterator().next(), equalTo(a1));
        assertThat(cdoManager.find(A.class, "2").iterator().next(), equalTo(a2));
        assertThat(cdoManager.find(A.class, "3").iterator().hasNext(), equalTo(false));
        cdoManager.commit();
    }

    @Test
    public void useIndexOf() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a1 = cdoManager.create(D.class);
        a1.setIndex("1");
        cdoManager.commit();
        cdoManager.begin();
        assertThat(cdoManager.find(D.class, "1").iterator().next(), equalTo(a1));
        cdoManager.commit();
    }


}
