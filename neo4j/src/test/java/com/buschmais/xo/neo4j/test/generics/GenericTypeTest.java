package com.buschmais.xo.neo4j.test.generics;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.generics.composite.BoundType;
import com.buschmais.xo.neo4j.test.generics.composite.GenericSuperType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class GenericTypeTest extends AbstractCdoManagerTest {

    public GenericTypeTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(GenericSuperType.class, BoundType.class);
    }

    @Test
    public void composite() {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        BoundType b = XOManager.create(BoundType.class);
        b.setValue("value");
        XOManager.currentTransaction().commit();
    }

}
