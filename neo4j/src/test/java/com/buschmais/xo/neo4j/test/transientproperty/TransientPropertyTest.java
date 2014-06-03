package com.buschmais.xo.neo4j.test.transientproperty;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.transientproperty.composite.A;

@RunWith(Parameterized.class)
public class TransientPropertyTest extends AbstractNeo4jXOManagerTest {

    public TransientPropertyTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    @Test
    public void transientProperty() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("persistent value");
        a.setTransientValue("transient value");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getValue(),equalTo("persistent value"));
        assertThat(a.getTransientValue(),equalTo("transient value"));
        xoManager.currentTransaction().commit();
        closeXOmanager();
        xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A result = xoManager.find(A.class, "persistent value").getSingleResult();
        assertThat(result.getValue(), equalTo("persistent value"));
        assertThat(result.getTransientValue(), nullValue());
        xoManager.currentTransaction().commit();
    }

}
