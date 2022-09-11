package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;

import java.util.Optional;

public interface DatabaseRepository extends Repository<DatabaseEntity, Long> {
    Optional<DatabaseEntity> findByName(String name);

    default DatabaseEntity findByOrCreateFromName(String name) {
        return findByName(name).orElseGet(() -> {
            DatabaseEntity newDatabase = manageNew();
            newDatabase.setName(name);
            return newDatabase;
        });
    }
}
