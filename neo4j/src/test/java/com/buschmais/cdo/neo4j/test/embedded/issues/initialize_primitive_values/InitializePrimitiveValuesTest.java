package com.buschmais.cdo.neo4j.test.embedded.issues.initialize_primitive_values;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.issues.initialize_primitive_values.composite.A;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/61
 */
public class InitializePrimitiveValuesTest extends
        AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[] { A.class };
    }

    @Test
    public void test() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        assertThat(a.getB(), nullValue());
        assertThat(a.isBoolean(), is(false));
        assertThat(a.getInt(), is(0));
        cdoManager.currentTransaction().commit();
    }

}
