package com.buschmais.xo.neo4j.test.issues.initialize_primitive_values;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.issues.initialize_primitive_values.composite.A;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * https://github.com/buschmais/cdo-neo4j/issues/61
 */
@RunWith(Parameterized.class)
public class InitializePrimitiveValuesIT extends AbstractNeo4JXOManagerIT {

    public InitializePrimitiveValuesIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class);
    }

    @Test
    public void test() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getB()).isNull();
        assertThat(a.isBoolean()).isFalse();
        assertThat(a.getInt()).isZero();
        xoManager.currentTransaction().commit();
    }

}
