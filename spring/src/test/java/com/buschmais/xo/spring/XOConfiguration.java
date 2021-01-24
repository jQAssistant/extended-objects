package com.buschmais.xo.spring;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.spring.model.Person;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XOConfiguration {

    public static final String NEO4J_DIRECTORY = "target/neo4j";

    private XOManagerFactory xoManagerFactory;

    @PostConstruct
    void init() throws URISyntaxException {
        XOUnit xoUnit = XOUnit.builder().provider(EmbeddedNeo4jXOProvider.class).uri(new URI("file:" + NEO4J_DIRECTORY)).type(Person.class).build();
        this.xoManagerFactory = XO.createXOManagerFactory(xoUnit);
    }

    @PreDestroy
    void destroy() {
        xoManagerFactory.close();
    }

    @Bean
    public XOManagerFactory getXOManagerFactory() {
        return xoManagerFactory;
    }

}
