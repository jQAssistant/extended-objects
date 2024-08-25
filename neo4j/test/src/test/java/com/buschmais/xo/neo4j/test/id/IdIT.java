package com.buschmais.xo.neo4j.test.id;

import java.util.Collection;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.id.composite.A;
import com.buschmais.xo.neo4j.test.id.composite.A2B;
import com.buschmais.xo.neo4j.test.id.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class IdIT extends AbstractNeo4JXOManagerIT {

    public IdIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, A2B.class);
    }

    @Test
    public void id() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        B b = xoManager.create(B.class);
        A2B a2b = xoManager.create(a, A2B.class, b);
        Object aId = xoManager.getId(a);
        assertThat(aId).isNotNull();
        assertThat((Object) ((CompositeObject) a).getId()).isEqualTo(aId);
        Object bId = xoManager.getId(b);
        assertThat(bId).isNotNull();
        assertThat((Object) ((CompositeObject) b).getId()).isEqualTo(bId);
        Object a2bId = xoManager.getId(a2b);
        assertThat(a2bId).isNotNull();
        assertThat((Object) ((CompositeObject) a2b).getId()).isEqualTo(a2bId);
    }
}
