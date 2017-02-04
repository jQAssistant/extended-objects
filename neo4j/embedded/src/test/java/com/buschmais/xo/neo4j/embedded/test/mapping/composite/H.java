package com.buschmais.xo.neo4j.embedded.test.mapping.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("H")
public interface H {

    @Relation("ONE_TO_ONE")
    @Incoming
    G getOneToOneG();

    void setOneToOneG(G g);

    @Incoming
    @Relation("ONE_TO_MANY")
    G getManyToOneG();

    void setManyToOneG(G g);

    @Incoming
    @Relation("MANY_TO_MANY")
    List<G> getManyToManyG();
}
