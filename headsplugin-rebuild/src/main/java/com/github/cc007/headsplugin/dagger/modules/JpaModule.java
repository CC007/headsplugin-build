package com.github.cc007.headsplugin.dagger.modules;

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
import com.github.cc007.headsplugin.integration.database.services.jpa.JpaManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.jpa.JpaQueryService;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;
import com.github.cc007.headsplugin.integration.database.transaction.jpa.JpaNestableTransaction;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Module
public abstract class JpaModule {

    @Provides
    @Singleton
    static EntityManagerFactory provideEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("default");
    }

    @Provides
    @Singleton
    static EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Provides
    @Singleton
    static Transaction provideEntityTransaction(EntityManager entityManager) {
        return new JpaNestableTransaction(entityManager);
    }

    @Provides
    @Singleton
    static QueryService provideQueryService(EntityManager entityManager) {
        return new JpaQueryService(entityManager);
    }

    @Provides
    @Singleton
    static ManagedEntityService provideManagedEntityService(EntityManager entityManager) {
        return new JpaManagedEntityService(entityManager);
    }

    @Provides
    @Singleton
    static CategoryRepository provideCategoryRepository(QueryService queryService, ManagedEntityService managedEntityService) {
        return new JpaCategoryRepository(queryService, managedEntityService);
    }

    @Provides
    @Singleton
    static DatabaseRepository provideDatabaseRepository(QueryService queryService, ManagedEntityService managedEntityService) {
        return new JpaDatabaseRepository(queryService, managedEntityService);
    }

    @Provides
    @Singleton
    static HeadRepository provideHeadRepository(QueryService queryService, ManagedEntityService managedEntityService) {
        return new JpaHeadRepository(queryService, managedEntityService);
    }

    @Provides
    @Singleton
    static SearchRepository provideSearchRepository(QueryService queryService, ManagedEntityService managedEntityService) {
        return new JpaSearchRepository(queryService, managedEntityService);
    }

    @Provides
    @Singleton
    static TagRepository provideTagRepository(QueryService queryService, ManagedEntityService managedEntityService) {
        return new JpaTagRepository(queryService, managedEntityService);
    }
}
