package com.buschmais.xo.neo4j.test.relation.typed;

import java.util.Collection;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.typed.composite.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RelationSubclassingIT extends AbstractNeo4JXOManagerIT {

    public RelationSubclassingIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(C.class, D.class, TypeA.class, TypeB.class);
    }

    @Test
    public void testRelationSubclassing() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        C c = xoManager.create(C.class);
        D d1 = xoManager.create(D.class);
        D d2 = xoManager.create(D.class);
        BaseType relation1 = xoManager.create(c, TypeA.class, d1);
        relation1.setVersion(1);
        BaseType relation2 = xoManager.create(c, TypeB.class, d2);
        relation2.setVersion(2);
        xoManager.currentTransaction()
            .commit();

        xoManager.currentTransaction()
            .begin();
        assertThat(c.getTypeA()
            .getVersion()).isEqualTo(relation1.getVersion());
        assertThat(c.getTypeB()
            .getVersion()).isEqualTo(relation2.getVersion());
        assertThat(relation1.getC()).isEqualTo(c);
        assertThat(relation1.getD()).isEqualTo(d1);
        assertThat(relation2.getC()).isEqualTo(c);
        assertThat(relation2.getD()).isEqualTo(d2);
        xoManager.currentTransaction()
            .commit();
    }

}
