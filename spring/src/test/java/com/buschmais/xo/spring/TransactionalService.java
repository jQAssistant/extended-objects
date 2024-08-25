package com.buschmais.xo.spring;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.spring.model.Person;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

public class TransactionalService {

    private final XOManager xoManager;

    @Transactional
    public void cleanUp() {
        xoManager.createQuery("MATCH (p:Person) DELETE p")
            .execute()
            .close();
    }

    @Transactional
    public void createPerson(String name, boolean rollbackUsingException) {
        xoManager.create(Person.class, p -> p.setName(name));
        if (rollbackUsingException) {
            throw new IllegalStateException("Rolling back.");
        }
    }

    @Transactional
    public Long countPersons() {
        return xoManager.createQuery("MATCH (p:Person) RETURN count(p) as count")
            .execute()
            .getSingleResult()
            .get("count", Long.class);
    }
}
