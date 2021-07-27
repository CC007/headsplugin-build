package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.config.EntityManagerConfig;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;
import com.github.cc007.headsplugin.integration.database.repositories.jpa.JpaCategoryRepository;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Module
public abstract class JpaModule {
    
    @Provides
    @Singleton
    static EntityManager provideEntityManager() {
        return new EntityManagerConfig().createEntityManagerFactory()
                .createEntityManager();
    }

    @Provides
    @Singleton
    static EntityTransaction provideEntityTransaction(EntityManager entityManager) {
        return entityManager.getTransaction();
    }

    @Provides
    @Singleton
    static CategoryRepository provideCategoryRepository(EntityManager entityManager) {
        return JpaCategoryRepository.builder()
                .entityManager(entityManager)
                .build();
    }
//
//    @Binds
//    public abstract HeadRepository bindHeadRepository(HeadRepositoryImpl headRepositoryImpl);
//
//    @Binds
//    public abstract DatabaseRepository bindDatabaseRepository(DatabaseRepositoryImpl databaseRepositoryImpl);
//
//    @Binds
//    public abstract SearchRepository bindSearchRepository(SearchRepositoryImpl searchRepositoryImpl);
}
