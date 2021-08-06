package com.github.cc007.headsplugin.integration.database.transaction;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class Transaction {

    private final EntityManager entityManager;

    public void begin() {
        entityManager.getTransaction().begin();
    }

    public void commit(boolean clearCache) {
        entityManager.getTransaction().commit();
        if (clearCache) {
            entityManager.clear();
        }
    }

}
