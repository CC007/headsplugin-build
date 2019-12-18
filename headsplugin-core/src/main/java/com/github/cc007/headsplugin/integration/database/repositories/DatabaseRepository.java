package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;

import org.springframework.data.repository.CrudRepository;

public interface DatabaseRepository extends CrudRepository<DatabaseEntity, Long> {
}
