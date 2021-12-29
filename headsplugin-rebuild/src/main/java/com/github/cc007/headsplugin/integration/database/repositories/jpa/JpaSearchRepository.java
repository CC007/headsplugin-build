package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.SearchEntity;
import com.github.cc007.headsplugin.integration.database.repositories.SearchRepository;
import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.QueryService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class JpaSearchRepository implements SearchRepository {

    private final QueryService queryService;
    private final ManagedEntityService managedEntityService;

    @Override
    public Optional<SearchEntity> findBySearchTerm(String searchTerm) {
        return queryService.findByProperty(SearchEntity.class, "searchTerm", searchTerm);
    }

    @Override
    public SearchEntity manageNew() {
        return managedEntityService.manageNew(SearchEntity.class);
    }
}
