package com.buschmais.xo.neo4j.embedded.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;

import com.buschmais.xo.api.XOException;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.configuration.Config;
import org.neo4j.configuration.GraphDatabaseInternalSettings;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.logging.LogProvider;

import static com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider.PROPERTY_XO_NEO4J_EMBEDDED_PLUGINS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public abstract class AbstractEmbeddedDatabaseManagementServiceFactory implements DatabaseManagementServiceFactory {

    private static final String PLUGIN_DIRECTORY = "plugins";

    @Override
    public final DatabaseManagementService createDatabaseManagementService(URI uri, Config config, Properties properties) {
        File directory = new File(URLDecoder.decode(uri.getSchemeSpecificPart(), UTF_8)).getAbsoluteFile();
        File pluginDirectory = initializePlugins(directory, properties);

        Map<Setting<?>, Object> settings = toSettings(config);
        settings.put(GraphDatabaseSettings.plugin_dir, pluginDirectory.toPath());
        settings.put(GraphDatabaseInternalSettings.track_cursor_close, false);

        return getDatabaseManagementService(directory.toPath(), settings, Slf4jLogProvider.INSTANCE);
    }

    private Map<Setting<?>, Object> toSettings(Config config) {
        Map<Setting<?>, Object> settings = new HashMap<>();
        for (Setting<?> setting : config.getDeclaredSettings()
            .values()) {
            if (config.isExplicitlySet(setting)) {
                settings.put(setting, config.get(setting));
            }
        }
        return settings;
    }

    protected abstract DatabaseManagementService getDatabaseManagementService(Path directory, Map<Setting<?>, Object> settings, LogProvider userLogProvider);

    private static File initializePlugins(File storeDir, Properties properties) {
        File pluginDirectory = new File(storeDir, PLUGIN_DIRECTORY);
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
            if (!pluginDirectory.exists() && !pluginDirectory.mkdirs()) {
                log.warn("Cannot create embedded Neo4j database plugin directory '{}'.", pluginDirectory.getAbsolutePath());
            }
            for (String plugin : Splitter.on(",")
                .trimResults()
                .splitToList(plugins)) {
                File sourceFile = new File(plugin);
                File destinationFile = new File(pluginDirectory, sourceFile.getName());
                if (!destinationFile.exists()) {
                    try {
                        copyFile(sourceFile, destinationFile);
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
        if (pluginDirectory.exists()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginDirectory.toPath())) {
                for (Path path : stream) {
                    if (!Files.isDirectory(path)) {
                        fileSet.add(path);
                    }
                }
            } catch (IOException e) {
                throw new XOException("Cannot list plugin directory " + pluginDirectory, e);
            }
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
