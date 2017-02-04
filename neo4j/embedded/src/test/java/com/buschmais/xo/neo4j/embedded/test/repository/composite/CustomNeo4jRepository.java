package com.buschmais.xo.neo4j.embedded.test.repository.composite;

import static com.buschmais.xo.api.annotation.ResultOf.Parameter;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.annotation.ImplementedBy;
import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.Neo4jRepository;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

@Repository
public interface CustomNeo4jRepository extends Neo4jRepository {

    @ResultOf
    @Cypher("match (a) where a.name={name} return a")
    A findByName(@Parameter("name") String name);

    @ImplementedBy(FindMethod.class)
    A find(String name);

    public class FindMethod implements ProxyMethod<XOManager> {

        @Override
        public Object invoke(XOManager xoManager, Object instance, Object[] args) throws Exception {
            Object arg = args[0];
            return xoManager.find(A.class, arg).getSingleResult();
        }
    }
}
