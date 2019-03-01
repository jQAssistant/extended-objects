package com.buschmais.xo.trace;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.impl.XOManagerFactoryImpl;
import com.buschmais.xo.trace.api.TraceDatastoreProvider;
import com.buschmais.xo.trace.composite.A;
import org.hamcrest.collection.IsArrayContaining;
import org.junit.Test;

import java.util.Set;

import static com.buschmais.xo.api.Transaction.TransactionAttribute.NONE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class BootstrapTest {

    @Test
    public void traceProvider() {
        XOManagerFactory XOManagerFactory = XO.createXOManagerFactory("traceProvider");
        assertThat(XOManagerFactory, not(equalTo(null)));
        XOManagerFactoryImpl xoManagerFactoryImpl = (XOManagerFactoryImpl) XOManagerFactory;
        XOUnit xoUnit = xoManagerFactoryImpl.getXOUnit();
        assertThat(xoUnit.getName(), equalTo("traceProvider"));
        assertThat(xoUnit.getProvider(), typeCompatibleWith(TraceDatastoreProvider.class));
        Set<? extends Class<?>> types = xoUnit.getTypes();
        assertThat(types.size(), equalTo(1));
        assertThat(types.toArray(), IsArrayContaining.<Object>hasItemInArray(A.class));
        assertThat(xoUnit.getDefaultTransactionAttribute(), equalTo(NONE));
    }
}
