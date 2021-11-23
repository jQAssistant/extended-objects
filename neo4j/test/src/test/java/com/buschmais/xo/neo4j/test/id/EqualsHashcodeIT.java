package com.buschmais.xo.neo4j.test.id;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.id.composite.A;
import com.buschmais.xo.neo4j.test.id.composite.A2B;
import com.buschmais.xo.neo4j.test.id.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class EqualsHashcodeIT extends AbstractNeo4JXOManagerIT {

    public EqualsHashcodeIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void equalsHashcode() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        int aHashCode = a.hashCode();
        B b = xoManager.create(B.class);
        int bHashCode = b.hashCode();
        A2B a2b = xoManager.create(a, A2B.class, b);
        int a2bHashCode = a2b.hashCode();
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.equals(a), equalTo(true));
        assertThat(b.equals(b), equalTo(true));
        assertThat(a2b.equals(a2b), equalTo(true));
        assertThat(a.hashCode(), equalTo(aHashCode));
        assertThat(b.hashCode(), equalTo(bHashCode));
        assertThat(a2b.hashCode(), equalTo(a2bHashCode));
        xoManager.currentTransaction().commit();
    }
}
