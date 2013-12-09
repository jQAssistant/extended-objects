package com.buschmais.cdo.neo4j.test.embedded.mapping.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.api.annotation.ResultOf;

import java.util.List;

import static com.buschmais.cdo.api.Query.Result;
import static com.buschmais.cdo.api.annotation.ResultOf.Parameter;

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
}
