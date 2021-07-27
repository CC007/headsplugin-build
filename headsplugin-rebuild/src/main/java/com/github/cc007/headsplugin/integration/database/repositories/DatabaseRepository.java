package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;

import java.util.Optional;

public interface DatabaseRepository extends Repository<DatabaseEntity, Long> {
    Optional<DatabaseEntity> findByName(String name);
}
