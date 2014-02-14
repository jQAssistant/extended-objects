package com.buschmais.cdo.neo4j.test.migration;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.cdo.neo4j.test.migration.composite.A;
import com.buschmais.cdo.neo4j.test.migration.composite.B;
import com.buschmais.cdo.neo4j.test.migration.composite.C;
import com.buschmais.cdo.neo4j.test.migration.composite.D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class MigrationTest extends AbstractCdoManagerTest {

    public MigrationTest(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() throws URISyntaxException {
        return cdoUnits(A.class, B.class, C.class, D.class);
    }

    @Test
    public void downCast() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue("Value");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        B b = cdoManager.migrate(a, B.class);
        assertThat(a == b, equalTo(false));
        assertThat(b.getValue(), equalTo("Value"));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test
    public void compositeObject() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue("Value");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        B b = cdoManager.migrate(a, B.class, D.class).as(B.class);
        assertThat(b.getValue(), equalTo("Value"));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test
    public void migrationHandler() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue("Value");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        CdoManager.MigrationStrategy<A, C> migrationStrategy = new CdoManager.MigrationStrategy<A, C>() {
            @Override
            public void migrate(A instance, C target) {
                target.setName(instance.getValue());
            }
        };
        C c = cdoManager.migrate(a, migrationStrategy, C.class);
        assertThat(c.getName(), equalTo("Value"));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }

    @Test
    public void compositeObjectMigrationHandler() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        a.setValue("Value");
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        CdoManager.MigrationStrategy<A, C> migrationStrategy = new CdoManager.MigrationStrategy<A, C>() {
            @Override
            public void migrate(A instance, C target) {
                target.setName(instance.getValue());
            }
        };
        C c = cdoManager.migrate(a, migrationStrategy, C.class, D.class).as(C.class);
        assertThat(c.getName(), equalTo("Value"));
        cdoManager.currentTransaction().commit();
        cdoManager.close();
    }
}
