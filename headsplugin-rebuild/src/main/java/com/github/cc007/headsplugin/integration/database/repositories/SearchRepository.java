package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;

import java.util.Optional;

public interface SearchRepository extends Repository<SearchEntity, Long> {

    Optional<SearchEntity> findBySearchTerm(String searchTerm);
}
