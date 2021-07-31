package com.github.cc007.headsplugin.api;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.business.services.HelloService;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import org.apache.commons.collections4.Transformer;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public interface HeadsPluginServices {

    ConfigProperties configProperties();

    HelloService helloService();

    Transformer<CategoryEntity, Category> categoryEntityToCategoryMapper();

    CategoryRepository categoryRepository();

    DatabaseRepository databaseRepository();

    HeadRepository headRepository();

    EntityManager entityManager();

    EntityTransaction entityTransaction();

    //StartupCategoryUpdater startupCategoryUpdater();
}
