package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public interface SearchRepository extends Repository<SearchEntity, Long> {

    Optional<SearchEntity> findBySearchTerm(String searchTerm);

    default SearchEntity findByOrCreateFromSearchTerm(String searchTerm) {
        return findBySearchTerm(searchTerm).orElseGet(() -> {
            SearchEntity newSearch = manageNew();
            newSearch.setSearchTerm(searchTerm);
            newSearch.resetSearchCount();
            newSearch.setLastUpdated(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
            return newSearch;
        });
    }
}
