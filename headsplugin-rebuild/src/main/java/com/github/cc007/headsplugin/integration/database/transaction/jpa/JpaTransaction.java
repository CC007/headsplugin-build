package com.github.cc007.headsplugin.integration.database.transaction.jpa;

import com.github.cc007.headsplugin.integration.database.transaction.Transaction;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class JpaTransaction implements Transaction {

    private final EntityManager entityManager;

    @Override
    public void begin() {
        entityManager.getTransaction().begin();
    }

    @Override
    public void commit(boolean clearCache) {
        entityManager.getTransaction().commit();
        if (clearCache) {
            entityManager.clear();
        }
    }

}
