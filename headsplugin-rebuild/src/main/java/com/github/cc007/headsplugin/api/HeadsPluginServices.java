package com.github.cc007.headsplugin.api;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.business.services.HelloService;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;

import org.apache.commons.collections4.Transformer;

public interface HeadsPluginServices {

    ConfigProperties configProperties();

    HelloService helloService();

    Transformer<CategoryEntity, Category> categoryEntityToCategoryMapper();

    //StartupCategoryUpdater startupCategoryUpdater();
}
