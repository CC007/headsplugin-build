package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.api.HeadsPluginServices;
import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;

import lombok.extern.log4j.Log4j2;
import org.apache.openjpa.persistence.EntityExistsException;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
public class HeadsPlugin extends JavaPlugin implements HeadsPluginApi {

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

        //StartupCategoryUpdater startupCategoryUpdater = headsPluginComponent.startupCategoryUpdater();
        //startupCategoryUpdater.update();

        experiments();
    }

    @Override
    public void onDisable() {
        shutdownDatabase();
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
    }

    @Override
    public HeadsPluginComponent getHeadsPluginServices() {
        return headsPluginComponent;
    }

    private void shutdownDatabase() {
        EntityManager entityManager = getHeadsPluginServices().entityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("CHECKPOINT;").executeUpdate();
        entityManager.createNativeQuery("SHUTDOWN;").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private void experiments() {
        HeadsPluginApi headsPluginApi = HeadsPluginApi.getPlugin().orElseThrow(IllegalStateException::new);
        HeadsPluginServices headsPluginServices = headsPluginApi.getHeadsPluginServices();

        CategoryRepository categoryRepository = headsPluginServices.categoryRepository();
        EntityTransaction entityTransaction = headsPluginServices.entityTransaction();
        try {
            entityTransaction.begin();
            CategoryEntity category = categoryRepository.manageNew();
            category.setName("MyCategory");
            category.setLastUpdated(LocalDateTime.now());
            entityTransaction.commit();
        } catch (RollbackException e) {
            if (e.getCause() instanceof EntityExistsException) {
                log.warn("MyCategory was already added to the database");
            } else {
                throw e;
            }
        }
        Optional<CategoryEntity> myCategory = categoryRepository.findByName("MyCategory");
        if(myCategory.isPresent()) {
            log.info("Category found: " + myCategory);
        } else {
            log.error("No category found");
        }
    }
}
