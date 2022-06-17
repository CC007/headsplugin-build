package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.QueryService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class JpaDatabaseRepository implements DatabaseRepository {

    private final QueryService queryService;
    private final ManagedEntityService managedEntityService;

    @Override
    public Optional<DatabaseEntity> findByName(String name) {
        return queryService.findByProperty(DatabaseEntity.class, "name", name);
    }

    @Override
    public DatabaseEntity manageNew() {
        return managedEntityService.manageNew(DatabaseEntity.class);
    }
}
