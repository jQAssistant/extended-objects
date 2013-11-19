package com.buschmais.cdo.neo4j.test.rest.crud;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.test.rest.AbstractRestCdoManagerTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CrudTest extends AbstractRestCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Test
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
        cdoManager.delete(a);
        cdoManager.commit();
        cdoManager.begin();
        try {
            assertThat(a.getName(), equalTo("Bar"));
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
