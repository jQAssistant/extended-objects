package com.buschmais.cdo.impl.test.bootstrap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ParamTest {

    private String value;

    public ParamTest(String value) {
        this.value = value;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        List<Object[]> params = new ArrayList<>();
        params.add(new Object[]{"A"});
        params.add(new Object[]{"B"});
        return params;
    }

    @Test
    public void test() {
        System.out.println(value);
    }
}
