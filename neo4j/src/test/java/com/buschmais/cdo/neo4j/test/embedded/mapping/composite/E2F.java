package com.buschmais.cdo.neo4j.test.embedded.mapping.composite;

import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.api.annotation.Relation;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.annotation.ResultOf.Parameter;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Relation("E2F")
public interface E2F {

    @Outgoing
    E getE();

    @Incoming
    F getF();

    @ResultOf(query = ByValue.class, usingThisAs = "e2f")
    Result<ByValue> getResultByValueUsingExplicitQuery(@Parameter("value") String value);

    @ResultOf(usingThisAs = "e2f")
    Result<ByValue> getResultByValueUsingReturnType(@Parameter("value") String value);

    @ResultOf(query = ByValue.class, usingThisAs = "e2f")
    ByValue getByValueUsingExplicitQuery(@Parameter("value") String value);

    @ResultOf(usingThisAs = "e2f")
    ByValue getByValueUsingReturnType(@Parameter("value") String value);

    @ResultOf
    ByValueUsingImplicitThis getByValueUsingImplicitThis(@Parameter("value") String value);

    @ResultOf
    @Cypher("match ()-[e2f:E2F]->(f:F) where e2f.value={value} return f")
    Result<F> getResultUsingCypher(@Parameter("value") String value);

    @ResultOf
    @Cypher("match ()-[e2f:E2F]->(f:F) where e2f.value={value} return f")
    F getSingleResultUsingCypher(@Parameter("value") String value);

    void setValue(String value);

    String getValue();

    @Cypher("match ()-[e2f:E2F]->(f:F) where e2f={e2f} and e2f.value={value} return f")
    public interface ByValue {
        F getF();
    }

    @Cypher("match ()-[e2f:E2F]->(f:F) where e2f={this} and e2f.value={value} return f")
    public interface ByValueUsingImplicitThis {
        F getF();
    }
}
