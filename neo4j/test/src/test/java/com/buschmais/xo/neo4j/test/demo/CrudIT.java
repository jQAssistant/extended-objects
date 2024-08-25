package com.buschmais.xo.neo4j.test.demo;

import java.util.Collection;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class CrudIT extends AbstractNeo4JXOManagerIT {

    public CrudIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class);
    }

    @Test
    public void create() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setName("Foo");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        a = xoManager.find(A.class, "Foo")
            .getSingleResult();
        assertThat(a.getName()).isEqualTo("Foo");
        a.setName("Bar");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        xoManager.createQuery("match (a:A) where a.name=$name return a")
            .withParameter("name", "Bar")
            .execute()
            .getSingleResult();
        xoManager.delete(a);
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        try {
            xoManager.createQuery("match (a:A) return a")
                .execute()
                .getSingleResult();
            fail("An exception is expected.");
        } catch (XOException e) {
        }
        xoManager.currentTransaction()
            .commit();
    }

    @Label("A")
    public interface A {

        @Indexed
        String getName();

        void setName(String name);

    }
}
