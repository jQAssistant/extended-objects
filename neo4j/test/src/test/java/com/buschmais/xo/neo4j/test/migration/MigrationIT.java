package com.buschmais.xo.neo4j.test.migration;

import java.util.Collection;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.migration.composite.A;
import com.buschmais.xo.neo4j.test.migration.composite.B;
import com.buschmais.xo.neo4j.test.migration.composite.C;
import com.buschmais.xo.neo4j.test.migration.composite.D;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MigrationIT extends AbstractNeo4JXOManagerIT {

    public MigrationIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, C.class, D.class);
    }

    @Test
    public void downCast() {
        XOManager xoManager = getXOManagerFactory().createXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setValue("Value");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        B b = xoManager.migrate(a)
            .add(B.class)
            .as(B.class);
        assertThat(a).isNotSameAs(b);
        assertThat(a.getValue()).isEqualTo("Value");
        assertThat(b.getValue()).isEqualTo("Value");
        xoManager.currentTransaction()
            .commit();
        xoManager.close();
    }

    @Test
    public void compositeObject() {
        XOManager xoManager = getXOManagerFactory().createXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setValue("Value");
        xoManager.currentTransaction()
            .commit();
        xoManager.currentTransaction()
            .begin();
        B b = xoManager.migrate(a)
            .add(B.class, D.class)
            .as(B.class);
        assertThat(b.getValue()).isEqualTo("Value");
        xoManager.currentTransaction()
            .commit();
        xoManager.close();
    }

    @Test
    public void addType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        A a = xoManager.create(A.class);
        a.setValue("Value");
        assertThat(a).isInstanceOf(A.class)
            .isNotInstanceOf(C.class);
        CompositeObject compositeObject = xoManager.migrate(a)
            .add(C.class);
        assertThat(compositeObject).isInstanceOf(A.class)
            .isInstanceOf(C.class);
        assertThat(a.getValue()).isEqualTo("Value");
        assertThat(compositeObject.as(A.class)
            .getValue()).isEqualTo("Value");
        xoManager.currentTransaction()
            .commit();
        xoManager.close();
    }

    @Test
    public void removeType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        CompositeObject compositeObject = xoManager.create(A.class, C.class);
        assertThat(compositeObject).isInstanceOf(A.class)
            .isInstanceOf(C.class);
        compositeObject.as(A.class)
            .setValue("Value");
        A a = xoManager.migrate(compositeObject)
            .remove(C.class)
            .as(A.class);
        assertThat(a).isInstanceOf(A.class)
            .isNotInstanceOf(C.class);
        assertThat((Object) compositeObject.getId()).isEqualTo(((CompositeObject) a).getId());
        assertThat(a.getValue()).isEqualTo("Value");
        xoManager.currentTransaction()
            .commit();
        xoManager.close();
    }
}
