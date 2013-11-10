package com.buschmais.cdo.neo4j.test.generics;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.generics.composite.BoundType;
import com.buschmais.cdo.neo4j.test.generics.composite.GenericSuperType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class GenericTypeTest extends AbstractCdoManagerTest {

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
