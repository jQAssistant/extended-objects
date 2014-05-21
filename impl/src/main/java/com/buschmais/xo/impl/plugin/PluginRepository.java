package com.buschmais.xo.impl.plugin;

/**
 * Interface for plugin repositories providing methods for registration, unregistration and lookup.
 */
public interface PluginRepository<Key, Plugin> {

    /**
     * Initializes the plugin repository.
     */
    void init();

    /**
     * Return the type of plugins managed by this repository.
     *
     * @return The type of plugins.
     */
    Class<? super Plugin> getPluginType();

    /**
     * Register a plugin.
     *
     * @param plugin The plugin instance.
     * @return The key used to identify the plugin.
     */
    Key register(Plugin plugin);

    /**
     * Register a plugin by its type.
     *
     * @param pluginType The plugin type.
     * @return The key used to identify the plugin.
     */
    Key register(Class<Plugin> pluginType);

    /**
     * Unregister a plugin.
     *
     * @param key The key.
     */
    void unregister(Key key);

    /**
     * Lookup a plugin.
     *
     * @param key The key.
     * @return The plugin.
     */
    Plugin get(Key key);
}
