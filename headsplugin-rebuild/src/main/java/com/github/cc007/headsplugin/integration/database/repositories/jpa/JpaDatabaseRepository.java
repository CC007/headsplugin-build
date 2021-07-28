package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;

import lombok.experimental.SuperBuilder;

import java.util.Optional;

@SuperBuilder
public class JpaDatabaseRepository extends AbstractRepository<DatabaseEntity, Long> implements DatabaseRepository {

    @Override
    public Optional<DatabaseEntity> findByName(String name) {
        return findBy("name", name);
    }

}
