package com.buschmais.xo.impl.test.bootstrap;

import static com.buschmais.xo.api.ConcurrencyMode.MULTITHREADED;
import static com.buschmais.xo.api.Transaction.TransactionAttribute.MANDATORY;
import static com.buschmais.xo.api.ValidationMode.NONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import java.util.Set;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.impl.test.bootstrap.composite.A;
import com.buschmais.xo.impl.test.bootstrap.provider.TestXOProvider;

import org.junit.Test;

public class BootstrapTest {

    @Test
    public void testUnit() {
        XOManagerFactory XOManagerFactory = XO.createXOManagerFactory("testUnit");
        assertThat(XOManagerFactory, not(equalTo(null)));
        XOManagerFactoryImpl xoManagerFactoryImpl = (XOManagerFactoryImpl) XOManagerFactory;
        XOUnit xoUnit = xoManagerFactoryImpl.getXOUnit();
        assertThat(xoUnit.getName(), equalTo("testUnit"));
        assertThat(xoUnit.getDescription(), equalTo("This is a test unit."));
        assertThat(xoUnit.getUri().toString(), equalTo("file://foo"));
        assertThat(xoUnit.getProvider(), typeCompatibleWith(TestXOProvider.class));
        Set<? extends Class<?>> types = xoUnit.getTypes();
        assertThat(types.size(), equalTo(1));
        assertThat(types.toArray(), hasItemInArray(A.class));
        assertThat(xoUnit.getValidationMode(), equalTo(NONE));
        assertThat(xoUnit.getConcurrencyMode(), equalTo(MULTITHREADED));
        assertThat(xoUnit.getDefaultTransactionAttribute(), equalTo(MANDATORY));
        assertThat(xoUnit.getProperties(), hasEntry(equalTo((Object) "foo"), equalTo((Object) "bar")));
    }
}
