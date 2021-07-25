package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;

import lombok.extern.log4j.Log4j2;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

@Log4j2
public class HeadsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        HeadsPluginComponent headsPluginComponent = DaggerHeadsPluginComponent.create();
        ConfigProperties configProperties = headsPluginComponent.configProperties();
        log.info("HeadsPluginAPI version: " + configProperties.getVersion());
        //StartupCategoryUpdater startupCategoryUpdater = headsPluginComponent.startupCategoryUpdater();
        //startupCategoryUpdater.update();
    }

    /**
     * Gets a plugin
     *
     * @param pluginName Name of the plugin to get
     * @return Optional of the plugin from name
     */
    public static Optional<Plugin> getPlugin(String pluginName) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        if (plugin != null && plugin.isEnabled()) {
            return Optional.of(plugin);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<HeadsPlugin> getPlugin() {
        return getPlugin("HeadsPluginAPI")
                .filter(plugin -> plugin instanceof HeadsPlugin)
                .map(plugin -> (HeadsPlugin) plugin);
    }
}
