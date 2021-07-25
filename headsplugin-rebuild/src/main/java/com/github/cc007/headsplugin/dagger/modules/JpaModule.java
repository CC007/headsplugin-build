package com.github.cc007.headsplugin.dagger.modules;

import dagger.Module;
import dagger.Provides;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Module
public abstract class JpaModule {
    
    private static class EntityManagerLoader {
        
        private EntityManagerFactory getEntityManagerFactory() {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
            return entityManagerFactory;
        }
    }
    
    @Provides
    static EntityManager entityManager() {
        return new EntityManagerLoader().getEntityManagerFactory().createEntityManager();
    }

//    @Binds
//    public abstract CategoryRepository bindCategoryRepository(CategoryRepositoryImpl categoryRepositoryImpl);
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
