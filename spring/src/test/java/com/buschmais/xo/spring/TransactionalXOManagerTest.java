package com.buschmais.xo.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neo4j.configuration.GraphDatabaseInternalSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TransactionalService.class, XOConfiguration.class, XOAutoConfiguration.class, XOTransactionManager.class })
public class TransactionalXOManagerTest {

    @Autowired
    private TransactionalService transactionalService;

    @BeforeEach
    void cleanUp() {
        System.out.println(GraphDatabaseInternalSettings.track_cursor_close);
        transactionalService.cleanUp();
    }

    @Test
    void commitSingleTransaction() {
        transactionalService.createPerson("Indiana Jones", false);
        assertThat(transactionalService.countPersons()).isEqualTo(1);
    }

    @Test
    void commitMultipleTransactions() {
        int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            transactionalService.createPerson("Indiana Jones", false);
        }
        assertThat(transactionalService.countPersons()).isEqualTo(iterations);
    }

    @Test
    void rollbackSingleTransaction() {
        try {
            transactionalService.createPerson("Indiana Jones", true);
            fail("Expecting an " + IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(transactionalService.countPersons()).isEqualTo(0);
        }
    }
}
