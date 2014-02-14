package com.buschmais.cdo.neo4j.test.generics;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.generics.composite.BoundType;
import com.buschmais.cdo.neo4j.test.generics.composite.GenericSuperType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class GenericTypeTest extends AbstractCdoManagerTest {

    public GenericTypeTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(GenericSuperType.class, BoundType.class);
    }

    @Test
    public void composite() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        BoundType b = cdoManager.create(BoundType.class);
        b.setValue("value");
        cdoManager.currentTransaction().commit();
    }

}
