package com.github.cc007.headsplugin.dagger;

import com.github.cc007.headsplugin.api.HeadsPluginServices;
import com.github.cc007.headsplugin.dagger.modules.ConfigModule;
import com.github.cc007.headsplugin.dagger.modules.FeignModule;
import com.github.cc007.headsplugin.dagger.modules.ServiceModule;
import com.github.cc007.headsplugin.dagger.modules.api.ApiServiceModule;
import com.github.cc007.headsplugin.dagger.modules.jpa.EntityModule;
import com.github.cc007.headsplugin.dagger.modules.jpa.RepositoryModule;
import com.github.cc007.headsplugin.dagger.modules.source.FreshCoalModule;
import com.github.cc007.headsplugin.dagger.modules.source.MineSkinModule;
import com.github.cc007.headsplugin.dagger.modules.source.MinecraftHeadsModule;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;
import com.github.cc007.headsplugin.integration.database.repositories.TagRepository;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import dagger.Component;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

@Singleton
@Component(modules = {
        ApiServiceModule.class,
        ConfigModule.class,
        FeignModule.class,
        RepositoryModule.class,
        ServiceModule.class,
        EntityModule.class,
        FreshCoalModule.class,
        MineSkinModule.class,
        MinecraftHeadsModule.class
})
public interface HeadsPluginComponent extends HeadsPluginServices {

    CategoryRepository categoryRepository();

    DatabaseRepository databaseRepository();

    HeadRepository headRepository();

    TagRepository tagRepository();

    SearchRepository searchRepository();

    EntityManager entityManager();

    Transaction transaction();

    //StartupCategoryUpdater startupCategoryUpdater();
}
