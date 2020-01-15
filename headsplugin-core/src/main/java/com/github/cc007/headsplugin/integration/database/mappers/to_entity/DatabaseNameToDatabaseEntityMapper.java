package com.github.cc007.headsplugin.integration.database.mappers.to_entity;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Categorizable;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseNameToDatabaseEntityMapper implements Transformer<String, DatabaseEntity> {

    private final DatabaseRepository databaseRepository;

    @Override
    public DatabaseEntity transform(String databaseName) {
        val optionalDatabase = databaseRepository.findByName(databaseName);

        DatabaseEntity database;
        if (!optionalDatabase.isPresent()) {
            database = new DatabaseEntity();
            database.setName(databaseName);
        } else {
            database = optionalDatabase.get();
        }
        return databaseRepository.save(database);
    }
}
