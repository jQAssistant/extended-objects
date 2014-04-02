package com.buschmais.xo.neo4j.test.mapping.composite;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.annotation.ResultOf.Parameter;

@Label("E")
public interface E {

    @Relation("RELATED_TO")
    List<F> getRelatedTo();

    @ResultOf(query = ByValue.class, usingThisAs = "e")
    Result<ByValue> getResultByValueUsingExplicitQuery(@Parameter("value") String value);

    @ResultOf(usingThisAs = "e")
    Result<ByValue> getResultByValueUsingReturnType(@Parameter("value") String value);

    @ResultOf(query = ByValue.class, usingThisAs = "e")
    ByValue getByValueUsingExplicitQuery(@Parameter("value") String value);

    @ResultOf(usingThisAs = "e")
    ByValue getByValueUsingReturnType(@Parameter("value") String value);

    @ResultOf
    ByValueUsingImplicitThis getByValueUsingImplicitThis(@Parameter("value") String value);

    @ResultOf
    @Cypher("match (e:E)-[:RELATED_TO]-(f:F) where f.value={value} return f")
    Result<F> getResultUsingCypher(@Parameter("value") String value);

    @ResultOf
    @Cypher("match (e:E)-[:RELATED_TO]-(f:F) where f.value={value} return f")
    F getSingleResultUsingCypher(@Parameter("value") String value);

    List<E2F> getE2F();

    @Cypher("match (e:E)-[:RELATED_TO]-(f:F) where id(e)={e} and f.value={value} return f")
    interface ByValue {
        F getF();
    }

    @Cypher("match (e:E)-[:RELATED_TO]-(f:F) where id(e)={this} and f.value={value} return f")
    interface ByValueUsingImplicitThis {
        F getF();
    }
}
