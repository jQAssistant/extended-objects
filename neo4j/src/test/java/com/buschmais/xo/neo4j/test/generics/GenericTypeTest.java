package com.buschmais.xo.neo4j.test.generics;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
import com.buschmais.xo.neo4j.test.generics.composite.BoundType;
import com.buschmais.xo.neo4j.test.generics.composite.GenericSuperType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class GenericTypeTest extends AbstractXOManagerTest {

    public GenericTypeTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(GenericSuperType.class, BoundType.class);
    }

    @Test
    public void composite() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        BoundType b = xoManager.create(BoundType.class);
        b.setValue("value");
        xoManager.currentTransaction().commit();
    }

}
