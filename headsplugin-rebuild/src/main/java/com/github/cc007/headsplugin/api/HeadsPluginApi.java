package com.github.cc007.headsplugin.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public interface HeadsPluginApi {

    /**
     * Gets a plugin
     *
     * @param pluginName Name of the plugin to get
     * @return Optional of the plugin from name
     */
    static Optional<Plugin> getPlugin(String pluginName) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        if (plugin != null && plugin.isEnabled()) {
            return Optional.of(plugin);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets the instance of the HeadsPluginApi
     * @return Optional of the HeadsPluginApi plugin
     */
    static Optional<HeadsPluginApi> getPlugin() {
        return getPlugin("HeadsPluginAPI")
                .filter(plugin -> plugin instanceof HeadsPluginApi)
                .map(plugin -> (HeadsPluginApi) plugin);
    }

    /**
     * Get the services provider for the this plugin.
     *
     * These services include things like searching for heads, updating categories, etc.
     *
     * @return the services provider
     */
    HeadsPluginServices getHeadsPluginServices();

}
