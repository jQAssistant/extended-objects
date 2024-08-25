package com.buschmais.xo.neo4j.test.query;

import java.util.Collection;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.Neo4jDatabase;
import com.buschmais.xo.neo4j.test.query.composite.B;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.ConstraintViolationException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class UniqueIT extends AbstractNeo4JXOManagerIT {

    public UniqueIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(asList(Neo4jDatabase.MEMORY), asList(B.class));
    }

    @Before
    public void createConstraint() {
        getXOManager().currentTransaction()
            .begin();
        try (Query.Result<Query.Result.CompositeRowObject> ignored = getXOManager().createQuery("CREATE CONSTRAINT FOR (b:B) REQUIRE b.uniqueValue IS UNIQUE")
            .execute()) {
        }
        getXOManager().currentTransaction()
            .commit();
    }

    @Test(expected = ConstraintViolationException.class)
    public void denyDuplicates() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        B a1 = xoManager.create(B.class);
        a1.setUniqueValue("A1");
        B a2_1 = xoManager.create(B.class);
        a2_1.setUniqueValue("A2");
        B a2_2 = xoManager.create(B.class);
        a2_2.setUniqueValue("A2");
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void index() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        B a1 = xoManager.create(B.class);
        a1.setValue("A1");
        B a2_1 = xoManager.create(B.class);
        a2_1.setValue("A2");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        B a = xoManager.find(B.class, "A1")
            .getSingleResult();
        assertThat(a).isEqualTo(a1);
        try {
            xoManager.find(B.class, "A3")
                .getSingleResult();
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {

        }
        xoManager.currentTransaction()
            .commit();
    }
}
