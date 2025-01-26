package com.buschmais.xo.neo4j.test.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.api.model.Neo4jLabel;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.query.composite.A;
import com.buschmais.xo.neo4j.test.query.composite.InstanceByValue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.buschmais.xo.api.Query.Result;
import static com.buschmais.xo.api.Query.Result.CompositeRowObject;
import static com.buschmais.xo.neo4j.test.query.CustomQueryLanguagePlugin.CustomQueryLanguage;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class QueryIT extends AbstractNeo4JXOManagerIT {

    private A a1;
    private A a2_1;
    private A a2_2;

    public QueryIT(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(A.class);
    }

    @Before
    public void createData() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        a1 = xoManager.create(A.class);
        a1.setValue("A1");
        a2_1 = xoManager.create(A.class);
        a2_1.setValue("A2");
        a2_2 = xoManager.create(A.class);
        a2_2.setValue("A2");
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void typedQuery() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<InstanceByValue> result = xoManager.createQuery(InstanceByValue.class)
            .withParameter("value", "A1")
            .execute();
        A a = result.getSingleResult()
            .getA();
        assertThat(a.getValue()).isEqualTo("A1");
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void compositeRowAsMap() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<CompositeRowObject> result = xoManager.createQuery("match (a:A) where a.value=$value return a, a.value as value, id(a) as id")
            .withParameter("value", "A1")
            .execute();
        CompositeRowObject singleResult = result.getSingleResult();
        List<String> columns = singleResult.getColumns();
        assertThat(columns).isEqualTo(Arrays.asList("a", "value", "id"));
        A a = singleResult.get("a", A.class);
        assertThat(a.getValue()).isEqualTo("A1");
        result = xoManager.createQuery("match (a:A) where a.Value=$value return a")
            .withParameter("value", "A2")
            .execute();
        try {
            result.getSingleResult()
                .get("a", A.class);
            fail("Expecting a " + XOException.class.getName());
        } catch (XOException e) {
        }
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void instanceParameter() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<CompositeRowObject> row = xoManager.createQuery("match (a:A) where id(a)=$instance return a")
            .withParameter("instance", a1)
            .execute();
        A a = row.getSingleResult()
            .get("a", A.class);
        assertThat(a).isEqualTo(a1);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void instanceParameterCollection() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<CompositeRowObject> row = xoManager.createQuery("match (a:A) where a.value in $values return a")
            .withParameter("values", Arrays.asList("A1"))
            .execute();
        A a = row.getSingleResult()
            .get("a", A.class);
        assertThat(a).isEqualTo(a1);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void optionalMatch() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<CompositeRowObject> row = getXOManager().createQuery("OPTIONAL MATCH (a:A) WHERE a.name = 'X' return a")
            .execute();
        A a = row.getSingleResult()
            .get("a", A.class);
        assertThat(a).isNull();
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void customQueryLanguage() {
        XOManager xoManager = getXOManager();
        xoManager.currentTransaction()
            .begin();
        Result<CompositeRowObject> row = getXOManager().createQuery("A:value=A1")
            .using(CustomQueryLanguage.class)
            .execute();
        A a = row.getSingleResult()
            .get("A", A.class);
        assertThat(a).isEqualTo(a1);
        xoManager.currentTransaction()
            .commit();
    }

    @Test
    public void nonTransactionalQuery() {
        XOManager xoManager = getXOManager();

        A a = xoManager.createQuery("MATCH (a:A{value:$value}) CALL { WITH a SET a:Custom } IN TRANSACTIONS RETURN a")
            .withParameter("value", "A1")
            .execute()
            .getSingleResult()
            .get("a", A.class);

        assertThat(a).isEqualTo(a1);
        xoManager.currentTransaction()
            .begin();
        assertThat(a.getValue()).isEqualTo("A1");
        Neo4jNode<Neo4jLabel, ?, ?, ?> delegate = ((CompositeObject) a).getDelegate();
        Set<String> labels = stream(delegate.getLabels()
            .spliterator(), false).map(neo4jLabel -> neo4jLabel.getName())
            .collect(toSet());
        assertThat(labels).containsExactlyInAnyOrder("A", "Custom");
        xoManager.currentTransaction()
            .commit();
    }

    public interface ResultPart1 {
        A getA();
    }

    public interface ResultPart2 {
        Number getCount();
    }
}
