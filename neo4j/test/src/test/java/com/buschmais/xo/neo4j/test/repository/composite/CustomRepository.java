package com.buschmais.xo.neo4j.test.repository.composite;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.annotation.ImplementedBy;
import com.buschmais.xo.api.annotation.Repository;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

import static com.buschmais.xo.api.annotation.ResultOf.Parameter;

@Repository
public interface CustomRepository {

    @ResultOf
    @Cypher("match (a) where a.name=$name return a")
    A findByName(@Parameter("name") String name);

    @ImplementedBy(FindMethod.class)
    A find(String name);

    class FindMethod implements ProxyMethod<XOManager> {

        @Override
        public Object invoke(XOManager xoManager, Object instance, Object[] args) {
            Object arg = args[0];
            return xoManager.find(A.class, arg)
                .getSingleResult();
        }
    }

    @ResultOf
    @Cypher("match (a) where a.name=$name return a, { a: a, name: a.name } as nestedProjection")
    Projection projection(String name);

    interface Projection {

        A getA();

        NestedProjection getNestedProjection();

    }

    interface NestedProjection {

        A getA();

        String getName();

    }
}
