package com.buschmais.xo.impl.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the available {@link com.buschmais.xo.impl.plugin.PluginRepository} instances.
 * <p>A {@link com.buschmais.xo.impl.plugin.PluginRepository} is identified by the plugin interface type it holds plugins.</p>
 */
public class PluginRepositoryManager {

    private Map<Class<?>, PluginRepository<?, ?>> pluginManagers = new HashMap<>();

    /**
     * Constructor.
     *
     * @param pluginRepositories The plugin repositories to manage.
     */
    public PluginRepositoryManager(PluginRepository<?, ?>... pluginRepositories) {
        for (PluginRepository<?, ?> pluginRepository : pluginRepositories) {
            this.pluginManagers.put(pluginRepository.getPluginType(), pluginRepository);
        }
    }

    /**
     * Return a plugin repository identified by the plugin interface type.
     *
     * @param pluginType The plugin interface.
     * @param <P>        The plugin type
     * @return The {@link com.buschmais.xo.impl.plugin.PluginRepository}.
     */
    public <P extends PluginRepository<?, ?>> P getPluginManager(Class<?> pluginType) {
        return (P) pluginManagers.get(pluginType);
    }

}
