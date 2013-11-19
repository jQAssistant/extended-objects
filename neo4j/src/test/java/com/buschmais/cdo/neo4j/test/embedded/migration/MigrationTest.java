package com.buschmais.cdo.neo4j.test.embedded.migration;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.migration.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.migration.composite.B;
import com.buschmais.cdo.neo4j.test.embedded.migration.composite.C;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class MigrationTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class, B.class, C.class};
    }

    @Test
    public void downCast() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setValue("Value");
        cdoManager.commit();
        cdoManager.begin();
        B b = cdoManager.migrate(a, B.class);
        assertThat(a == b, equalTo(false));
        assertThat(b.getValue(), equalTo("Value"));
        cdoManager.commit();
        cdoManager.close();
    }

    @Test
    public void migrationHandler() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setValue("Value");
        cdoManager.commit();
        cdoManager.begin();
        CdoManager.MigrationStrategy<A, C> migrationStrategy = new CdoManager.MigrationStrategy<A, C>() {
            @Override
            public void migrate(A instance, C target) {
                target.setName(instance.getValue());
            }
        };
        C c = cdoManager.migrate(a, migrationStrategy, C.class);
        assertThat(c.getName(), equalTo("Value"));
        cdoManager.commit();
        cdoManager.close();
    }

    @Test
    public void compositeObjectMigrationHandler() {
        CdoManager cdoManager = getCdoManagerFactory().createCdoManager();
        cdoManager.begin();
        A a = cdoManager.create(A.class);
        a.setValue("Value");
        cdoManager.commit();
        cdoManager.begin();
        CdoManager.MigrationStrategy<A, CompositeObject> migrationStrategy = new CdoManager.MigrationStrategy<A, CompositeObject>() {
            @Override
            public void migrate(A instance, CompositeObject target) {
                target.as(C.class).setName(instance.getValue());
            }
        };
        C c = cdoManager.migrate(a, migrationStrategy, CompositeObject.class, C.class).as(C.class);
        assertThat(c.getName(), equalTo("Value"));
        cdoManager.commit();
        cdoManager.close();
    }
}
