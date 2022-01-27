package com.github.cc007.headsplugin.dagger.modules.jpa;

import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;
import com.github.cc007.headsplugin.integration.database.repositories.TagRepository;
import com.github.cc007.headsplugin.integration.database.repositories.jpa.JpaCategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.jpa.JpaDatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.jpa.JpaHeadRepository;
import com.github.cc007.headsplugin.integration.database.repositories.jpa.JpaSearchRepository;
import com.github.cc007.headsplugin.integration.database.repositories.jpa.JpaTagRepository;
import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.QueryService;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public abstract class RepositoryModule {

    @Provides
    @Singleton
    static CategoryRepository provideCategoryRepository(
            QueryService queryService,
            ManagedEntityService managedEntityService
    ) {
        return new JpaCategoryRepository(queryService, managedEntityService);
    }

    @Provides
    @Singleton
    static DatabaseRepository provideDatabaseRepository(
            QueryService queryService,
            ManagedEntityService managedEntityService
    ) {
        return new JpaDatabaseRepository(queryService, managedEntityService);
    }

    @Provides
    @Singleton
    static HeadRepository provideHeadRepository(
            QueryService queryService,
            ManagedEntityService managedEntityService,
            ConfigProperties configProperties
    ) {
        return new JpaHeadRepository(queryService, managedEntityService, configProperties);
    }

    @Provides
    @Singleton
    static SearchRepository provideSearchRepository(
            QueryService queryService,
            ManagedEntityService managedEntityService
    ) {
        return new JpaSearchRepository(queryService, managedEntityService);
    }

    @Provides
    @Singleton
    static TagRepository provideTagRepository(
            QueryService queryService,
            ManagedEntityService managedEntityService
    ) {
        return new JpaTagRepository(queryService, managedEntityService);
    }
}
