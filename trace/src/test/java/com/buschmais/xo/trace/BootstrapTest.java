package com.buschmais.xo.trace;

import static com.buschmais.xo.api.Transaction.TransactionAttribute.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.typeCompatibleWith;

import java.util.Set;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.trace.api.TraceDatastoreProvider;
import com.buschmais.xo.trace.composite.A;

import org.junit.Test;

public class BootstrapTest {

    @Test
    public void traceProvider() {
        XOManagerFactory XOManagerFactory = XO.createXOManagerFactory("traceProvider");
        assertThat(XOManagerFactory).isNotNull();
        XOManagerFactoryImpl xoManagerFactoryImpl = (XOManagerFactoryImpl) XOManagerFactory;
        XOUnit xoUnit = xoManagerFactoryImpl.getXOUnit();
        assertThat(xoUnit.getName()).isEqualTo("traceProvider");
        assertThat(xoUnit.getProvider(), typeCompatibleWith(TraceDatastoreProvider.class));
        Set<? extends Class<?>> types = xoUnit.getTypes();
        assertThat(types).hasSize(1);
        assertThat(types.toArray()).contains(A.class);
        assertThat(xoUnit.getDefaultTransactionAttribute()).isEqualTo(NONE);
    }
}
