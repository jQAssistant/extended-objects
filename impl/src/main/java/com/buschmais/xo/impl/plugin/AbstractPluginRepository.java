package com.buschmais.xo.impl.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.buschmais.xo.spi.reflection.ClassHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base implementation of a {@link PluginRepository}.
 */
public abstract class AbstractPluginRepository<Key, Plugin> implements PluginRepository<Key, Plugin> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPluginRepository.class);

    private Map<Key, Plugin> plugins;

    /**
     * Constructor.
     */
    public AbstractPluginRepository() {
        this.plugins = new ConcurrentHashMap<>();
    }

    @Override
    public Key register(Plugin plugin) {
        Key key = getKey(plugin);
        LOGGER.debug("Registering plugin for " + key);
        if (key != null) {
            this.plugins.put(key, plugin);
        }
        return key;
    }

    @Override
    public Key register(Class<Plugin> pluginType) {
        return register(ClassHelper.newInstance(pluginType));
    }

    @Override
    public void unregister(Key key) {
        LOGGER.debug("Unregistering plugin for " + key);
        plugins.remove(key);
    }

    @Override
    public Plugin get(Key key) {
        return plugins.get(key);
    }

    protected abstract Key getKey(Plugin plugin);
}
