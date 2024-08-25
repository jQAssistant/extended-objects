package com.buschmais.xo.neo4j.test.validation;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.validation.composite.A;
import com.buschmais.xo.neo4j.test.validation.composite.B;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ValidationModeNoneIT extends AbstractNeo4JXOManagerIT {

    public ValidationModeNoneIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(asList(A.class, B.class), Collections.emptyList(), ValidationMode.NONE, ConcurrencyMode.SINGLETHREADED,
                Transaction.TransactionAttribute.MANDATORY);
    }

    @Test
    public void validationOnCommitAfterInsert() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getName()).isNull();
        xoManager.currentTransaction().commit();
    }

}
