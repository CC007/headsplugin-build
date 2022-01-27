package com.github.cc007.headsplugin.dagger.modules.jpa;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.CategoryEntityToCategoryMapper;
import com.github.cc007.headsplugin.integration.database.mappers.from_entity.HeadEntityToHeadMapper;
import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.QueryService;
import com.github.cc007.headsplugin.integration.database.services.jpa.JpaManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.jpa.JpaQueryService;
import com.github.cc007.headsplugin.integration.database.transaction.Transaction;
import com.github.cc007.headsplugin.integration.database.transaction.jpa.JpaNestableTransaction;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.collections4.Transformer;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Module
public abstract class EntityModule {

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
    static Transaction provideTransaction(EntityManager entityManager, Thread mainThread) {
        return new JpaNestableTransaction(entityManager, mainThread);
    }

    @Provides
    @Singleton
    static Transformer<CategoryEntity, Category> provideCategoryEntityToCategoryMapper() {
        return new CategoryEntityToCategoryMapper();
    }

    @Provides
    @Singleton
    static Transformer<HeadEntity, Head> provideHeadEntityToHeadMapper() {
        return new HeadEntityToHeadMapper();
    }


}
