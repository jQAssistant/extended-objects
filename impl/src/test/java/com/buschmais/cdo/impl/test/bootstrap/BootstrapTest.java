package com.buschmais.cdo.impl.test.bootstrap;

import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.CdoManagerFactoryImpl;
import com.buschmais.cdo.impl.test.bootstrap.composite.A;
import com.buschmais.cdo.impl.test.bootstrap.provider.TestCdoProvider;
import org.hamcrest.collection.IsArrayContaining;
import org.junit.Test;

import java.util.Set;

import static com.buschmais.cdo.api.ConcurrencyMode.MULTITHREADED;
import static com.buschmais.cdo.api.Transaction.TransactionAttribute.MANDATORY;
import static com.buschmais.cdo.api.ValidationMode.NONE;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

public class BootstrapTest {

    @Test
    public void bootstrap() {
        CdoManagerFactory cdoManagerFactory = Cdo.createCdoManagerFactory("testUnit");
        assertThat(cdoManagerFactory, not(equalTo(null)));
        CdoManagerFactoryImpl cdoManagerFactoryImpl = (CdoManagerFactoryImpl) cdoManagerFactory;
        CdoUnit cdoUnit = cdoManagerFactoryImpl.getCdoUnit();
        assertThat(cdoUnit.getName(), equalTo("testUnit"));
        assertThat(cdoUnit.getDescription(), equalTo("This is a test unit."));
        assertThat(cdoUnit.getUri().toString(), equalTo("file://foo"));
        assertThat(cdoUnit.getProvider(), typeCompatibleWith(TestCdoProvider.class));
        Set<? extends Class<?>> types = cdoUnit.getTypes();
        assertThat(types.size(), equalTo(1));
        assertThat(types.toArray(), IsArrayContaining.<Object>hasItemInArray(A.class));
        assertThat(cdoUnit.getValidationMode(), equalTo(NONE));
        assertThat(cdoUnit.getConcurrencyMode(), equalTo(MULTITHREADED));
        assertThat(cdoUnit.getDefaultTransactionAttribute(), equalTo(MANDATORY));
        assertThat(cdoUnit.getProperties(), hasEntry(equalTo((Object) "foo"), equalTo((Object) "bar")));
    }

}
