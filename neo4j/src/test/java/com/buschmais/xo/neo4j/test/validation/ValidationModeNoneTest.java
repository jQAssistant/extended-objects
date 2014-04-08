package com.buschmais.xo.neo4j.test.validation;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.annotation.PreUpdate;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractXOManagerTest;
import com.buschmais.xo.neo4j.test.relation.typed.composite.TreeNode;
import com.buschmais.xo.neo4j.test.relation.typed.composite.TreeNodeRelation;
import com.buschmais.xo.neo4j.test.validation.composite.A;
import com.buschmais.xo.neo4j.test.validation.composite.B;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ValidationModeNoneTest extends AbstractXOManagerTest {

    public ValidationModeNoneTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(asList(A.class, B.class), Collections.<Class<?>>emptyList(), ValidationMode.NONE, ConcurrencyMode.SINGLETHREADED, Transaction.TransactionAttribute.MANDATORY);
    }

    @Test
    public void validationOnCommitAfterInsert() {
        XOManager xoManager = getXoManager();
        xoManager.currentTransaction().begin();
        A a = xoManager.create(A.class);
        xoManager.currentTransaction().commit();
        xoManager.currentTransaction().begin();
        assertThat(a.getName(), nullValue());
        xoManager.currentTransaction().commit();
    }

}
