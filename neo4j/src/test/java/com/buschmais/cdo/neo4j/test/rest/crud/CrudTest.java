package com.buschmais.cdo.neo4j.test.rest.crud;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.test.rest.AbstractRestCdoManagerTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CrudTest extends AbstractRestCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test
    @Ignore("neo4j-rest-graphdb is not available for Neo4j 2.0.0-RC1 and M06 is not compatible.")
    public void create() throws InterruptedException {
        CdoManager cdoManager = getCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setName("Foo");
        cdoManager.commit();
        cdoManager.begin();
        a = cdoManager.find(A.class, "Foo").getSingleResult();
        assertThat(a.getName(), equalTo("Foo"));
        a.setName("Bar");
        cdoManager.commit();
        cdoManager.begin();
        cdoManager.createQuery("match (a:A) where a.name={name} return a").withParameter("name", "Bar").execute().getSingleResult();
        cdoManager.delete(a);
        cdoManager.commit();
        cdoManager.begin();
        try {
            cdoManager.createQuery("match (a:A) return a").execute().getSingleResult();
            Assert.fail("An exception is expected.");
        } catch (CdoException e) {
        }
        cdoManager.commit();
    }

    @Label("A")
    public interface A {

        @Indexed
        String getName();

        void setName(String name);

    }
}
