package com.buschmais.cdo.neo4j.test.embedded.generics;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.generics.composite.BoundType;
import com.buschmais.cdo.neo4j.test.embedded.generics.composite.GenericSuperType;
import org.junit.Test;

public class GenericTypeTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{GenericSuperType.class, BoundType.class};
    }

    @Test
    public void composite() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        BoundType b = cdoManager.create(BoundType.class);
        b.setValue("value");
        cdoManager.commit();
    }

}
