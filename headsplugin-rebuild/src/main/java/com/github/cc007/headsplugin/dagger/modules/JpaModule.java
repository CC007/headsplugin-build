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
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

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
        return new Transaction(entityManager);
    }

    @Provides
    @Singleton
    static CategoryRepository provideCategoryRepository(EntityManager entityManager) {
        return JpaCategoryRepository.builder()
                .entityManager(entityManager)
                .build();
    }

    @Provides
    @Singleton
    static DatabaseRepository provideDatabaseRepository(EntityManager entityManager) {
        return JpaDatabaseRepository.builder()
                .entityManager(entityManager)
                .build();
    }

    @Provides
    @Singleton
    static HeadRepository provideHeadRepository(EntityManager entityManager) {
        return JpaHeadRepository.builder()
                .entityManager(entityManager)
                .build();
    }

    @Provides
    @Singleton
    static SearchRepository provideSearchRepository(EntityManager entityManager) {
        return JpaSearchRepository.builder()
                .entityManager(entityManager)
                .build();
    }

    @Provides
    @Singleton
    static TagRepository provideTagRepository(EntityManager entityManager) {
        return JpaTagRepository.builder()
                .entityManager(entityManager)
                .build();
    }
}
