package com.buschmais.cdo.impl.test.bootstrap;


import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;
import com.buschmais.cdo.spi.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.test.bootstrap.composite.A;
import com.buschmais.cdo.impl.test.bootstrap.provider.TestCdoProvider;
import org.junit.Test;

import static com.buschmais.cdo.api.CdoManagerFactory.ValidationMode.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class BootstrapTest {

    @Test
    public void bootstrap() {
        CdoManagerFactory cdoManagerFactory = Cdo.createCdoManagerFactory("testUnit");
        assertThat(cdoManagerFactory, not(equalTo(null)));
        CdoManagerFactoryImpl cdoManagerFactoryImpl= (CdoManagerFactoryImpl) cdoManagerFactory;
        CdoUnit cdoUnit = cdoManagerFactoryImpl.getCdoUnit();
        assertThat(cdoUnit.getName(), equalTo("testUnit"));
        assertThat(cdoUnit.getDescription(), equalTo("This is a test unit."));
        assertThat(cdoUnit.getUrl().toExternalForm(), equalTo("file://foo"));
        assertThat(cdoUnit.getProvider(), typeCompatibleWith(TestCdoProvider.class));
        assertThat(cdoUnit.getTypes(), hasItem(A.class));
        assertThat(cdoUnit.getValidationMode(), equalTo(NONE));
        assertThat(cdoUnit.getProperties(), hasEntry(equalTo((Object) "foo"), equalTo((Object) "bar")));
    }

}
