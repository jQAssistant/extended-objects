package com.buschmais.xo.impl.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Manages the available {@link com.buschmais.xo.impl.plugin.PluginRepository}
 * instances.
 * <p>
 * A {@link com.buschmais.xo.impl.plugin.PluginRepository} is identified by the
 * plugin interface type it holds plugins.
 * </p>
 */
public class PluginRepositoryManager {

    private Map<Class<?>, PluginRepository<?, ?>> pluginRepositories = new HashMap<>();

    /**
     * Constructor.
     *
     * @param pluginRepositories
     *     The plugin repositories to manage.
     */
    public <Plugin> PluginRepositoryManager(PluginRepository<?, Plugin>... pluginRepositories) {
        for (PluginRepository<?, Plugin> pluginRepository : pluginRepositories) {
            Class<? super Plugin> pluginType = pluginRepository.getPluginType();
            this.pluginRepositories.put(pluginType, pluginRepository);
            for (Plugin plugin : (Iterable<Plugin>) ServiceLoader.load(pluginType)) {
                pluginRepository.register(plugin);
            }
        }
    }

    /**
     * Return a plugin repository identified by the plugin interface type.
     *
     * @param pluginType
     *     The plugin interface.
     * @param <P>
     *     The plugin type
     * @return The {@link com.buschmais.xo.impl.plugin.PluginRepository}.
     */
    public <P extends PluginRepository<?, ?>> P getPluginManager(Class<?> pluginType) {
        return (P) pluginRepositories.get(pluginType);
    }

}
