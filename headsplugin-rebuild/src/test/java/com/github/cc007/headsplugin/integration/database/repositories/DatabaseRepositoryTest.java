package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.github.cc007.headsplugin.integration.database.entities.DatabaseEntityMatcher.aDatabaseEntityThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseRepositoryTest {

    @Spy
    DatabaseRepository databaseRepository = new DummyDatabaseRepository();

    @Test
    void findByOrCreateFromNameDatabaseNotFound() {
        // prepare
        val testDatabaseName = "DatabaseName";
        val testDatabaseEntity = new DatabaseEntity();

        when(databaseRepository.findByName(testDatabaseName))
                .thenReturn(Optional.empty());
        when(databaseRepository.manageNew())
                .thenReturn(testDatabaseEntity);

        // execute
        val actual = databaseRepository.findByOrCreateFromName(testDatabaseName);

        // verify
        assertThat(actual, is(aDatabaseEntityThat()
                .hasName(testDatabaseName)));
    }

    @Test
    void findByOrCreateFromNameDatabaseFound() {
        // prepare
        val testDatabaseName = "DatabaseName";

        val testDatabaseEntity = new DatabaseEntity();
        testDatabaseEntity.setName(testDatabaseName);

        when(databaseRepository.findByName(testDatabaseName))
                .thenReturn(Optional.of(testDatabaseEntity));

        // execute
        val actual = databaseRepository.findByOrCreateFromName(testDatabaseName);

        // verify
        assertThat(actual, is(aDatabaseEntityThat()
                .hasName(testDatabaseName)));
    }

}
class DummyDatabaseRepository implements DatabaseRepository {

    @Override
    public Optional<DatabaseEntity> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public DatabaseEntity manageNew() {
        return null;
    }

    @Override
    public DatabaseEntity manage(DatabaseEntity entity) {
        return null;
    }
}