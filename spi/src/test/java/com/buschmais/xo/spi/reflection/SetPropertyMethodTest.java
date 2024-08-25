package com.buschmais.xo.spi.reflection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.reflection.GetPropertyMethod;
import com.buschmais.xo.api.metadata.reflection.SetPropertyMethod;

import org.junit.Before;
import org.junit.Test;

public class SetPropertyMethodTest {

    private SetPropertyMethod setPropertyMethod;

    private SetPropertyMethod setInvalidPropertyMethod;

    @Before
    public void setUp() throws NoSuchMethodException {
        GetPropertyMethod getPropertyMethod = new GetPropertyMethod(TestClass.class.getMethod("getProperty"), "property", String.class, String.class);
        setPropertyMethod = new SetPropertyMethod(TestClass.class.getMethod("setProperty", String.class), getPropertyMethod, "property", String.class,
                String.class);
        GetPropertyMethod getInvalidPropertyMethod = new GetPropertyMethod(TestClass.class.getMethod("getInvalidProperty"), "invalidProperty", String.class,
                String.class);
        setInvalidPropertyMethod = new SetPropertyMethod(TestClass.class.getMethod("setInvalidProperty", String.class), getInvalidPropertyMethod,
                "invalidProperty", String.class, String.class);
    }

    @Test
    public void getAnnotation() {
        TestAnnotation annotation = setPropertyMethod.getAnnotation(TestAnnotation.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    public void getByMetaAnnotation() {
        TestAnnotation annotation = setPropertyMethod.getByMetaAnnotation(TestMetaAnnotation.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    public void getAnnotationOnSetter() {
        try {
            TestAnnotation annotation = setInvalidPropertyMethod.getAnnotation(TestAnnotation.class);
            fail("Expecting " + XOException.class);
        } catch (XOException e) {
            assertThat(e.getMessage()).contains("setInvalidProperty");
        }
    }

    @Test
    public void getByMetaAnnotationOnSetter() {
        try {
            TestAnnotation annotation = setInvalidPropertyMethod.getByMetaAnnotation(TestMetaAnnotation.class);
            fail("Expecting " + XOException.class);
        } catch (XOException e) {
            assertThat(e.getMessage()).contains("setInvalidProperty");
        }
    }

    @Test
    public void getAnnotations() {
        Annotation[] annotations = setPropertyMethod.getAnnotations();
        assertThat(annotations).isNotNull().hasSize(1);
        assertThat(annotations[0]).isInstanceOf(TestAnnotation.class);
    }

}
