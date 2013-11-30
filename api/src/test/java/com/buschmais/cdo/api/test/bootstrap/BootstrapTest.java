package com.buschmais.cdo.api.test.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.api.test.bootstrap.composite.A;
import com.buschmais.cdo.api.test.bootstrap.provider.TestCdoManagerFactory;
import com.buschmais.cdo.api.test.bootstrap.provider.TestCdoProvider;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

public class BootstrapTest {

    @Test
    public void bootstrap() {
        CdoManagerFactory cdoManagerFactory = Cdo.createCdoManagerFactory("testUnit");
        assertThat(cdoManagerFactory, not(equalTo(null)));
        TestCdoManagerFactory testCdoManagerFactory = (TestCdoManagerFactory) cdoManagerFactory;
        CdoUnit cdoUnit = testCdoManagerFactory.getCdoUnit();
        assertThat(cdoUnit.getName(), equalTo("testUnit"));
        assertThat(cdoUnit.getDescription(), equalTo("This is a test unit."));
        assertThat(cdoUnit.getUrl().toExternalForm(), equalTo("file://foo"));
        assertThat(cdoUnit.getProvider(), typeCompatibleWith(TestCdoProvider.class));
        assertThat(cdoUnit.getTypes(), IsCollectionContaining.hasItem(A.class));
        assertThat(cdoUnit.getValidationMode(), equalTo(CdoUnit.ValidationMode.NONE));
        assertThat(cdoUnit.getProperties(), hasEntry(equalTo((Object) "foo"), equalTo((Object) "bar")));
    }

}
