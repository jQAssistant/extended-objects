package com.buschmais.xo.neo4j.test.issues.url_encoding;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.XOManagerFactory;
import com.buschmais.xo.api.bootstrap.XO;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.api.bootstrap.XOUnitBuilder;
import com.buschmais.xo.neo4j.api.Neo4jXOProvider;

/**
 * https://github.com/buschmais/extended-objects/issues/129
 */
public class URLEncodingTest {

    private static final String DATABASE_PATH_CONTAINING_SPACES = "target/path containing spaces";

    private static final String DATABASE_URI_CONTAINING_SPACES = "file:target/path containing spaces";

    private File dbPath;

    @Before
    public void setup() throws Exception {
        dbPath = new File(DATABASE_PATH_CONTAINING_SPACES);
        if (dbPath.exists()) {
            assertThat(deleteRecursively(dbPath), is(true));
        }

        String encoded = dbPath.toURI().toURL().getPath();
        File encodedDbPath = new File(encoded);
        if (encodedDbPath.exists()) {
            assertThat(deleteRecursively(encodedDbPath), is(true));
        }
    }

    @Test
    public void testEncodedDatabasePathContainingSpaces() throws Exception {
        String encodedPath = dbPath.toURI().toURL().toExternalForm();
        XOUnit xoUnit = XOUnitBuilder.create(encodedPath, Neo4jXOProvider.class).create();
        XOManagerFactory xoManagerFactory = XO.createXOManagerFactory(xoUnit);
        XOManager xoManager = xoManagerFactory.createXOManager();

        assertThat(dbPath.exists(), is(true));
    }

    @Test(expected = URISyntaxException.class)
    public void testDatabasePathContainingSpaces() throws Exception {
        XOUnitBuilder.create(DATABASE_URI_CONTAINING_SPACES, Neo4jXOProvider.class).create();
    }

    private boolean deleteRecursively(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        return file.delete();
    }
}
