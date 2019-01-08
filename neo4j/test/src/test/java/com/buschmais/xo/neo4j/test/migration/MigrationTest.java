package com.buschmais.xo.neo4j.test.migration;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4jXOManagerTest;
import com.buschmais.xo.neo4j.test.migration.composite.A;
import com.buschmais.xo.neo4j.test.migration.composite.B;
import com.buschmais.xo.neo4j.test.migration.composite.C;
import com.buschmais.xo.neo4j.test.migration.composite.D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class MigrationTest extends AbstractNeo4jXOManagerTest {

    public MigrationTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class, B.class, C.class, D.class);
    }

    @Test
    public void downCast() {
        XOManager xoManager = getXOManagerFactory().createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("Value");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        B b = xoManager.migrate(a).add(B.class).as(B.class);
        assertThat(a == b, equalTo(false));
        assertThat(a.getValue(), equalTo("Value"));
        assertThat(b.getValue(), equalTo("Value"));
        xoManager.currentTransaction().commit();
        xoManager.close();
    }

    @Test
    public void compositeObject() {
        XOManager xoManager = getXOManagerFactory().createXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("Value");
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        B b = xoManager.migrate(a).add(B.class, D.class).as(B.class);
        assertThat(b.getValue(), equalTo("Value"));
        xoManager.currentTransaction().commit();
        xoManager.close();
    }

    @Test
    public void addType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        a.setValue("Value");
		assertThat(a, instanceOf(A.class));
		assertThat(a, not(instanceOf(C.class)));
		CompositeObject compositeObject = xoManager.migrate(a).add(C.class);
		assertThat(compositeObject, instanceOf(A.class));
		assertThat(compositeObject, instanceOf(C.class));
        assertThat(a.getValue(), equalTo("Value"));
		assertThat(compositeObject.as(A.class).getValue(), equalTo("Value"));
        xoManager.currentTransaction().commit();
        xoManager.close();
    }

    @Test
    public void removeType() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
		CompositeObject compositeObject = xoManager.create(A.class, C.class);
		assertThat(compositeObject, instanceOf(A.class));
		assertThat(compositeObject, instanceOf(C.class));
        compositeObject.as(A.class).setValue("Value");
		A a = xoManager.migrate(compositeObject).remove(C.class).as(A.class);
		assertThat(a, instanceOf(A.class));
		assertThat(a, not(instanceOf(C.class)));
        assertThat(compositeObject.getId(), equalTo(((CompositeObject)a).getId()));
        assertThat(a.getValue(), equalTo("Value"));
        xoManager.currentTransaction().commit();
        xoManager.close();
    }
}
