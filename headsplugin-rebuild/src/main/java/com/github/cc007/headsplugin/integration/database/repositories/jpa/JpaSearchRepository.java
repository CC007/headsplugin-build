package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;

import lombok.experimental.SuperBuilder;

import java.util.Optional;

@SuperBuilder
public class JpaSearchRepository extends AbstractRepository<SearchEntity, Long> implements SearchRepository {
    @Override
    public Optional<SearchEntity> findBySearchTerm(String searchTerm) {
        return findBy("searchTerm", searchTerm);
    }
}
