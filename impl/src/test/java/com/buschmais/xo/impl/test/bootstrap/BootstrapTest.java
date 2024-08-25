package com.buschmais.xo.impl.test.bootstrap;

import static com.buschmais.xo.api.ConcurrencyMode.MULTITHREADED;
import static com.buschmais.xo.api.Transaction.TransactionAttribute.MANDATORY;
import static com.buschmais.xo.api.ValidationMode.NONE;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(XOManagerFactory).isNotNull();
        XOManagerFactoryImpl xoManagerFactoryImpl = (XOManagerFactoryImpl) XOManagerFactory;
        XOUnit xoUnit = xoManagerFactoryImpl.getXOUnit();
        assertThat(xoUnit.getName()).isEqualTo("testUnit");
        assertThat(xoUnit.getDescription()).isEqualTo("This is a test unit.");
        assertThat(xoUnit.getUri()).hasToString("file://foo");
        assertThat(xoUnit.getProvider()).isEqualTo(TestXOProvider.class);
        Set<? extends Class<?>> types = xoUnit.getTypes();
        assertThat(types).hasSize(1);
        assertThat(types.toArray()).contains(A.class);
        assertThat(xoUnit.getValidationMode()).isEqualTo(NONE);
        assertThat(xoUnit.getConcurrencyMode()).isEqualTo(MULTITHREADED);
        assertThat(xoUnit.getDefaultTransactionAttribute()).isEqualTo(MANDATORY);
        assertThat(xoUnit.getProperties()).containsEntry("foo", "bar");
    }
}
