package com.buschmais.xo.neo4j.test.migration;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.migration.composite.A;
import com.buschmais.xo.neo4j.test.migration.composite.B;
import com.buschmais.xo.neo4j.test.migration.composite.C;
import com.buschmais.xo.neo4j.test.migration.composite.D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class MigrationTest extends AbstractCdoManagerTest {

    public MigrationTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class, C.class, D.class);
    }

    @Test
    public void downCast() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setValue("Value");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        B b = XOManager.migrate(a, B.class);
        assertThat(a == b, equalTo(false));
        assertThat(b.getValue(), equalTo("Value"));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test
    public void compositeObject() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setValue("Value");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        B b = XOManager.migrate(a, B.class, D.class).as(B.class);
        assertThat(b.getValue(), equalTo("Value"));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test
    public void migrationHandler() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setValue("Value");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        XOManager.MigrationStrategy<A, C> migrationStrategy = new XOManager.MigrationStrategy<A, C>() {
            @Override
            public void migrate(A instance, C target) {
                target.setName(instance.getValue());
            }
        };
        C c = XOManager.migrate(a, migrationStrategy, C.class);
        assertThat(c.getName(), equalTo("Value"));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }

    @Test
    public void compositeObjectMigrationHandler() {
        XOManager XOManager = getXOManagerFactory().createXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        a.setValue("Value");
        XOManager.currentTransaction().commit();
        XOManager.currentTransaction().begin();
        XOManager.MigrationStrategy<A, C> migrationStrategy = new XOManager.MigrationStrategy<A, C>() {
            @Override
            public void migrate(A instance, C target) {
                target.setName(instance.getValue());
            }
        };
        C c = XOManager.migrate(a, migrationStrategy, C.class, D.class).as(C.class);
        assertThat(c.getName(), equalTo("Value"));
        XOManager.currentTransaction().commit();
        XOManager.close();
    }
}
