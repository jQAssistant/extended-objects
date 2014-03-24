package com.buschmais.xo.neo4j.test.concurrency;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.test.AbstractCdoManagerTest;
import com.buschmais.xo.neo4j.test.concurrency.composite.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.*;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class MultithreadedTest extends AbstractCdoManagerTest {

    public MultithreadedTest(XOUnit XOUnit) {
        super(XOUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCdoUnits() {
        return cdoUnits(asList(A.class), Collections.<Class<?>>emptyList(), ValidationMode.AUTO, ConcurrencyMode.MULTITHREADED, Transaction.TransactionAttribute.REQUIRES);
    }

    @Test
    public void instance() throws ExecutionException, InterruptedException {
        XOManager XOManager = getXOManager();
        XOManager.currentTransaction().begin();
        A a = XOManager.create(A.class);
        XOManager.currentTransaction().commit();
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Integer> future1 = executorService.submit(new Worker(a));
        TimeUnit.SECONDS.sleep(1);
        Future<Integer> future2 = executorService.submit(new Worker(a));
        assertThat(future1.get(), equalTo(1));
        assertThat(future2.get(), equalTo(2));
    }

    private static class Worker implements Callable<Integer> {

        private A a;

        private Worker(A a) {
            this.a = a;
        }

        @Override
        public Integer call() throws Exception {
            return a.incrementAndGet();
        }
    }
}
