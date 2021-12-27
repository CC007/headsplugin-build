package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

@Log4j2
public class HeadsPlugin extends JavaPlugin {

    private HeadsPluginComponent headsPluginComponent;
    private ClassLoader defaultClassLoader;

    /**
     * Gets the instance of the HeadsPlugin
     *
     * @return Optional of the HeadsPlugin plugin
     */
    public static Optional<HeadsPlugin> getPlugin() {
        return HeadsPluginApi.getPlugin("HeadsPluginAPI")
                .filter(plugin -> plugin instanceof HeadsPlugin)
                .map(plugin -> (HeadsPlugin) plugin);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        defaultClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        headsPluginComponent = DaggerHeadsPluginComponent.create();
        HeadsPluginApi.setHeadsPluginServices(headsPluginComponent);

        //StartupCategoryUpdater startupCategoryUpdater = headsPluginComponent.startupCategoryUpdater();
        //startupCategoryUpdater.update();
    }

    @Override
    public void onDisable() {
        shutdownDatabase();
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
    }

    private void shutdownDatabase() {
        val entityManager = headsPluginComponent.entityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("CHECKPOINT;").executeUpdate();
        entityManager.createNativeQuery("SHUTDOWN;").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
