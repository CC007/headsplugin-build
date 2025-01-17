package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.github.cc007.headsplugin.integration.database.entities.SearchEntityMatcher.aSearchEntityThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchRepositoryTest {

    @Spy
    SearchRepository searchRepository = new DummySearchRepository();

    @Test
    void findByOrCreateFromSearchTermSearchNotFound() {
        // prepare
        final var testSearchTerm = "SearchTerm";
        final var testSearchEntity = new SearchEntity();

        when(searchRepository.findBySearchTerm(testSearchTerm))
                .thenReturn(Optional.empty());
        when(searchRepository.manageNew())
                .thenReturn(testSearchEntity);

        // execute
        final var actual = searchRepository.findByOrCreateFromSearchTerm(testSearchTerm);

        // verify
        assertThat(actual, is(aSearchEntityThat()
                .hasSearchTerm(testSearchTerm)
                .hasSearchCount(0L)
                .hasLastUpdated(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC))));
    }

    @Test
    void findByOrCreateFromSearchTermSearchFound() {
        // prepare
        final var testSearchTerm = "SearchTerm";
        final var testLastUpdated = LocalDateTime.now();

        final var testSearchEntity = new SearchEntity();
        testSearchEntity.setSearchTerm(testSearchTerm);
        testSearchEntity.incrementSearchCount();
        testSearchEntity.incrementSearchCount();
        testSearchEntity.setLastUpdated(testLastUpdated);

        when(searchRepository.findBySearchTerm(testSearchTerm))
                .thenReturn(Optional.of(testSearchEntity));

        // execute
        final var actual = searchRepository.findByOrCreateFromSearchTerm(testSearchTerm);

        // verify
        assertThat(actual, is(aSearchEntityThat()
                .hasSearchTerm(testSearchTerm)
                .hasSearchCount(2L)
                .hasLastUpdated(testLastUpdated)));
    }
}

class  DummySearchRepository implements SearchRepository {

    @Override
    public Optional<SearchEntity> findBySearchTerm(String searchTerm) {
        return Optional.empty();
    }

    @Override
    public SearchEntity manageNew() {
        return null;
    }
}