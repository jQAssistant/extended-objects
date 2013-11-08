package com.buschmais.cdo.neo4j.test.composite.migration;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.composite.AbstractCdoManagerTest;
import org.junit.Test;

public class MigrationTest extends AbstractCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[] {A.class, B.class};
    }

    @Test
    public void ambiguousLabels() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setIndex("1");
        cdoManager.commit();
        cdoManager.close();
    }
}
