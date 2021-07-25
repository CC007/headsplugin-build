package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.api.HeadsPluginServices;
import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.dagger.DaggerHeadsPluginComponent;
import com.github.cc007.headsplugin.dagger.HeadsPluginComponent;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.Transformer;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Log4j2
public class HeadsPlugin extends JavaPlugin implements HeadsPluginApi {

    private HeadsPluginComponent headsPluginComponent;

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

        headsPluginComponent = DaggerHeadsPluginComponent.create();

        //StartupCategoryUpdater startupCategoryUpdater = headsPluginComponent.startupCategoryUpdater();
        //startupCategoryUpdater.update();

        experiments();
    }

    @Override
    public HeadsPluginComponent getHeadsPluginServices() {
        return headsPluginComponent;
    }

    private void experiments() {
        HeadsPluginApi headsPluginApi = HeadsPluginApi.getPlugin().orElseThrow(IllegalStateException::new);
        HeadsPluginServices headsPluginServices = headsPluginApi.getHeadsPluginServices();

        ConfigProperties configProperties = headsPluginServices.configProperties();

        log.info("HeadsPluginAPI version: " + configProperties.getVersion());
        log.info("Category update interval: "
                + configProperties.getHeadsplugin()
                .getSearch()
                .getUpdate()
                .getInterval());
        log.info("Pokemon custom category search terms: "
                + configProperties.getHeadsplugin()
                .getCategories()
                .getCustom()
                .stream()
                .filter(customCategoryProperties -> customCategoryProperties.getName().equals("pokemon-starters"))
                .flatMap(customCategoryProperties -> customCategoryProperties.getSearchTerms().stream())
                .collect(Collectors.joining(", ")));

        log.info(headsPluginComponent.helloService().getGreeting());

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("MyCategory");
        categoryEntity.setLastUpdated(LocalDateTime.now());

        DatabaseEntity databaseEntity = new DatabaseEntity();
        databaseEntity.setName("MyDB");
        databaseEntity.addCategory(categoryEntity);

        categoryEntity.addDatabase(databaseEntity);

        Transformer<CategoryEntity, Category> transformer = headsPluginServices.categoryEntityToCategoryMapper();
        Category category = transformer.transform(categoryEntity);
        log.info("Category name: " + category.getName());
        log.info("Sources: " + category.getSources());
    }
}
