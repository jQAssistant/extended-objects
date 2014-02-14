package com.buschmais.cdo.neo4j.test.issues.initialize_primitive_values;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.issues.initialize_primitive_values.composite.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/61
 */
@RunWith(Parameterized.class)
public class InitializePrimitiveValuesTest extends AbstractCdoManagerTest {

    public InitializePrimitiveValuesTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class);
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
