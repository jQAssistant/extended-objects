package com.buschmais.cdo.neo4j.test.embedded.concurrency;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.ConcurrencyMode;
import com.buschmais.cdo.api.Transaction;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.concurrency.composite.A;
import org.junit.Test;

import java.util.concurrent.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class MultithreadedTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{A.class};
    }

    @Override
    protected Transaction.TransactionAttribute getTransactionAttribute() {
        return Transaction.TransactionAttribute.REQUIRES;
    }

    @Override
    protected ConcurrencyMode getConcurrencyMode() {
        return ConcurrencyMode.MULTITHREADED;
    }

    @Test
    public void instance() throws ExecutionException, InterruptedException {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.create(A.class);
        cdoManager.currentTransaction().commit();
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
