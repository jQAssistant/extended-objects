package com.buschmais.xo.neo4j.embedded.test.issues.initialize_primitive_values;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.embedded.test.issues.initialize_primitive_values.composite.A;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/61
 */
@RunWith(Parameterized.class)
public class InitializePrimitiveValuesTest extends AbstractNeo4jXOManagerTest {

    public InitializePrimitiveValuesTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    @Test
    public void test() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getB(), nullValue());
        assertThat(a.isBoolean(), is(false));
        assertThat(a.getInt(), is(0));
        xoManager.currentTransaction().commit();
    }

}
