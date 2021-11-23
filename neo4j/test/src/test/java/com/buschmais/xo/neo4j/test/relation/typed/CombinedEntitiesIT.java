package com.buschmais.xo.neo4j.test.relation.typed;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.typed.composite.E;
import com.buschmais.xo.neo4j.test.relation.typed.composite.E2F;
import com.buschmais.xo.neo4j.test.relation.typed.composite.F;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CombinedEntitiesIT extends AbstractNeo4JXOManagerIT {

    public CombinedEntitiesIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(E.class, F.class, E2F.class);
    }

    @Test
    public void testRelationSubclassing() {
        XOManager xoManager = getXOManager();
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
