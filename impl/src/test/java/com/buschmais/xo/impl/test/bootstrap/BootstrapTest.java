package com.buschmais.xo.impl.test.bootstrap;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.impl.test.bootstrap.composite.A;
import com.buschmais.xo.impl.test.bootstrap.provider.TestXOProvider;
import org.hamcrest.collection.IsArrayContaining;
import org.junit.Test;

import java.util.Set;

import static com.buschmais.xo.api.ConcurrencyMode.MULTITHREADED;
import static com.buschmais.xo.api.Transaction.TransactionAttribute.MANDATORY;
import static com.buschmais.xo.api.ValidationMode.NONE;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

public class BootstrapTest {

    @Test
    public void bootstrap() {
        XOManagerFactory XOManagerFactory = XO.createXOManagerFactory("testUnit");
        assertThat(XOManagerFactory, not(equalTo(null)));
        XOManagerFactoryImpl cdoManagerFactoryImpl = (XOManagerFactoryImpl) XOManagerFactory;
        XOUnit XOUnit = cdoManagerFactoryImpl.getXOUnit();
        assertThat(XOUnit.getName(), equalTo("testUnit"));
        assertThat(XOUnit.getDescription(), equalTo("This is a test unit."));
        assertThat(XOUnit.getUri().toString(), equalTo("file://foo"));
        assertThat(XOUnit.getProvider(), typeCompatibleWith(TestXOProvider.class));
        Set<? extends Class<?>> types = XOUnit.getTypes();
        assertThat(types.size(), equalTo(1));
        assertThat(types.toArray(), IsArrayContaining.<Object>hasItemInArray(A.class));
        assertThat(XOUnit.getValidationMode(), equalTo(NONE));
        assertThat(XOUnit.getConcurrencyMode(), equalTo(MULTITHREADED));
        assertThat(XOUnit.getDefaultTransactionAttribute(), equalTo(MANDATORY));
        assertThat(XOUnit.getProperties(), hasEntry(equalTo((Object) "foo"), equalTo((Object) "bar")));
    }

}
