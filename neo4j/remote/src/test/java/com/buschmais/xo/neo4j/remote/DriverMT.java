package com.buschmais.xo.neo4j.remote;

import org.junit.Test;
import org.neo4j.driver.v1.*;

public class DriverMT {

    @Test
    public void query() {
        String username = "neo4j";
        String password = "admin";
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic(username, password));
        Session session = driver.session();
        StatementResult result = session.run("MATCH (m:Movie) RETURN m");
        for (Record record : result.list()) {
            Value m = record.get("m");
            System.out.println(m);
        }

    }

}
