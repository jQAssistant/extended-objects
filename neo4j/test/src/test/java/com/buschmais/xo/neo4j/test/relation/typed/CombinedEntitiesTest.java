package com.buschmais.xo.neo4j.test.relation.typed;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.relation.typed.composite.E;
import com.buschmais.xo.neo4j.test.relation.typed.composite.E2F;
import com.buschmais.xo.neo4j.test.relation.typed.composite.F;

@RunWith(Parameterized.class)
public class CombinedEntitiesTest extends AbstractNeo4jXOManagerTest {

    public CombinedEntitiesTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(E.class, F.class, E2F.class);
    }

    @Test
    public void testRelationSubclassing() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        E e = xoManager.create(E.class);
        F f = xoManager.create(F.class);
        E2F e2F = xoManager.create(e, E2F.class, f);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(e2F, notNullValue());
        assertThat(e2F.getE(), equalTo(e));
        assertThat(e2F.getF(), equalTo(f));
        xoManager.currentTransaction().commit();
    }

}
