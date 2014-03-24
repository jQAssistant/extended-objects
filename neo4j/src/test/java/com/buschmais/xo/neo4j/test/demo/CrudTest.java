package com.buschmais.xo.neo4j.test.demo;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CrudTest extends AbstractCdoManagerTest {

    public CrudTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
    }

    @Test
    public void create() throws InterruptedException {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setName("Foo");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        a = XOManager.find(A.class, "Foo").getSingleResult();
        assertThat(a.getName(), equalTo("Foo"));
        a.setName("Bar");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        XOManager.createQuery("match (a:A) where a.name={name} return a").withParameter("name", "Bar").execute().getSingleResult();
        XOManager.delete(a);
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        try {
            XOManager.createQuery("match (a:A) return a").execute().getSingleResult();
            Assert.fail("An exception is expected.");
        } catch (XOException e) {
        }
        XOManager.currentTransaction().commit();
    }

    @Label("A")
    public interface A {

        @Indexed
        String getName();

        void setName(String name);

    }
}
