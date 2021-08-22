package com.github.cc007.headsplugin.integration.database.services.jpa;

import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class JpaManagedEntityService implements ManagedEntityService {
    protected final EntityManager entityManager;

    @Override
    @SneakyThrows({IllegalAccessException.class, InstantiationException.class, InvocationTargetException.class, NoSuchMethodException.class,})
    public <E> E manageNew(Class<E> entityType) {
        E entity = entityType.getConstructor().newInstance();
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public <E> E manage(E entity) {
        return entityManager.merge(entity);
    }
}