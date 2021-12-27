package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;

import lombok.val;
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
        val testSearchTerm = "SearchTerm";
        val testSearchEntity = new SearchEntity();

        when(searchRepository.findBySearchTerm(testSearchTerm))
                .thenReturn(Optional.empty());
        when(searchRepository.manageNew())
                .thenReturn(testSearchEntity);

        // execute
        val actual = searchRepository.findByOrCreateFromSearchTerm(testSearchTerm);

        // verify
        assertThat(actual, is(aSearchEntityThat()
                .hasSearchTerm(testSearchTerm)
                .hasSearchCount(0L)
                .hasLastUpdated(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC))));
    }

    @Test
    void findByOrCreateFromSearchTermSearchFound() {
        // prepare
        val testSearchTerm = "SearchTerm";
        val testLastUpdated = LocalDateTime.now();

        val testSearchEntity = new SearchEntity();
        testSearchEntity.setSearchTerm(testSearchTerm);
        testSearchEntity.incrementSearchCount();
        testSearchEntity.incrementSearchCount();
        testSearchEntity.setLastUpdated(testLastUpdated);

        when(searchRepository.findBySearchTerm(testSearchTerm))
                .thenReturn(Optional.of(testSearchEntity));

        // execute
        val actual = searchRepository.findByOrCreateFromSearchTerm(testSearchTerm);

        // verify
        assertThat(actual, is(aSearchEntityThat()
                .hasSearchTerm(testSearchTerm)
                .hasSearchCount(2L)
                .hasLastUpdated(testLastUpdated)));
    }
}

class  DummySearchRepository implements SearchRepository {

    @Override
    public SearchEntity manageNew() {
        return null;
    }

    @Override
    public SearchEntity manage(SearchEntity entity) {
        return null;
    }

    @Override
    public Optional<SearchEntity> findBySearchTerm(String searchTerm) {
        return Optional.empty();
    }
}