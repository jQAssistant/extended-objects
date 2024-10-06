package com.buschmais.xo.neo4j.embedded.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.JarFile;

import com.buschmais.xo.api.XOException;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.neo4j.configuration.Config;
import org.neo4j.configuration.GraphDatabaseInternalSettings;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider.PROPERTY_XO_NEO4J_EMBEDDED_PLUGINS;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class FileDatabaseManagementServiceFactory implements DatabaseManagementServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDatabaseManagementServiceFactory.class);

    private static final String PLUGIN_DIRECTORY = "plugins";

    @Override
    public DatabaseManagementService createDatabaseManagementService(URI uri, Config config, Properties properties) {
        String path;
        try {
            path = URLDecoder.decode(uri.toURL()
                .getPath(), StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            throw new XOException("Cannot get path fro URI" + uri, e);
        }
        File storeDir = new File(path);
        storeDir.mkdirs();
        LOGGER.debug("Creating graph database service datastore for directory '{}'.", storeDir.getAbsolutePath());

        File pluginDirectory = initializePlugins(storeDir, properties);

        DatabaseManagementServiceBuilder databaseManagementServiceBuilder = new DatabaseManagementServiceBuilder(storeDir.toPath());
        databaseManagementServiceBuilder.setConfig(GraphDatabaseSettings.plugin_dir, pluginDirectory.toPath());
        databaseManagementServiceBuilder.setConfig(toSettings(config));
        databaseManagementServiceBuilder.setConfig(GraphDatabaseInternalSettings.track_cursor_close, false);
        databaseManagementServiceBuilder.setUserLogProvider(Slf4jLogProvider.INSTANCE);
        return databaseManagementServiceBuilder.build();
    }

    private static File initializePlugins(File storeDir, Properties properties) {
        File pluginDirectory = new File(storeDir, PLUGIN_DIRECTORY);
        pluginDirectory.mkdirs();
        copyPlugins(pluginDirectory, properties);
        Set<Path> pluginPaths = listPlugins(pluginDirectory);
        getClasspathAppender().accept(pluginPaths);
        return pluginDirectory;
    }

    /**
     * Copy additional plugins to the plugin directory.
     */
    private static void copyPlugins(File pluginDirectory, Properties properties) {
        String plugins = properties.getProperty(PROPERTY_XO_NEO4J_EMBEDDED_PLUGINS);
        if (isNotEmpty(plugins)) {
            for (String plugin : Splitter.on(",")
                .trimResults()
                .splitToList(plugins)) {
                File sourceFile = new File(plugin);
                File destinationFile = new File(pluginDirectory, sourceFile.getName());
                if (!destinationFile.exists()) {
                    try {
                        FileUtils.copyFile(sourceFile, destinationFile);
                    } catch (IOException e) {
                        throw new XOException("Cannot copy plugin " + sourceFile + " to " + destinationFile, e);
                    }
                }
            }
        }
    }

    /**
     * List all plugins in plugin directory.
     */
    private static Set<Path> listPlugins(File pluginDirectory) {
        Set<Path> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginDirectory.toPath())) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path);
                }
            }
        } catch (IOException e) {
            throw new XOException("Cannot list plugin directory " + pluginDirectory, e);
        }
        return fileSet;
    }

    /**
     * Provides a {@link Consumer} to add the {@link Path} of a JAR file to the {@link ClassLoader} used by Neo4j.
     */
    private static Consumer<Set<Path>> getClasspathAppender() {
        ClassLoader neo4jClassLoader = GraphDatabaseSettings.class.getClassLoader();
        log.debug("Using Neo4j classloader {}", neo4jClassLoader);
        if (neo4jClassLoader instanceof URLClassLoader) {
            return getURLClassLoaderAppender((URLClassLoader) neo4jClassLoader);
        } else {
            return getInstrumentationAppender();
        }
    }

    /**
     * Uses an existing {@link URLClassLoader} (e.g. Maven) by making the method addURL accessible.
     */
    private static Consumer<Set<Path>> getURLClassLoaderAppender(URLClassLoader classLoader) {
        Method method;
        try {
            method = classLoader.getClass()
                .getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException e) {
            throw new XOException("Cannot use URLClassLoader to extend classpath.", e);
        }
        method.setAccessible(true);
        Set<URL> existingUrls = Set.of(classLoader.getURLs());
        return paths -> {
            for (Path path : paths) {
                URL url;
                try {
                    url = path.toUri()
                        .toURL();
                } catch (MalformedURLException e) {
                    throw new IllegalStateException(e);
                }
                if (!existingUrls.contains(url)) {
                    try {
                        method.invoke(classLoader, url);
                    } catch (ReflectiveOperationException e) {
                        throw new XOException("Cannot add URL to classloader.", e);
                    }
                }
            }
        };
    }

    /**
     * Uses {@link java.lang.instrument.Instrumentation} if available (e.g. CLI).
     */
    private static Consumer<Set<Path>> getInstrumentationAppender() {
        return paths -> InstrumentationProvider.INSTANCE.getInstrumentation()
            .ifPresentOrElse(instrumentation -> {
                for (Path path : paths) {
                    JarFile jarFile;
                    try {
                        jarFile = new JarFile(path.toFile());
                    } catch (IOException e) {
                        throw new XOException("Cannot create JAR from file " + path.toAbsolutePath(), e);
                    }
                    instrumentation.appendToSystemClassLoaderSearch(jarFile);

                }
            }, () -> log.warn("Runtime instrumentation is not available, Neo4j plugins might not work."));
    }
}
