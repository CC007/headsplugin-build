package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.TagEntity;
import com.github.cc007.headsplugin.integration.database.repositories.TagRepository;
import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.QueryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class JpaTagRepository implements TagRepository {

    private final QueryService queryService;
    private final ManagedEntityService managedEntityService;

    @Override
    public List<TagEntity> findAllByName(String name) {
        return queryService.findAllByProperty(TagEntity.class, "name", name);
    }

    @Override
    public TagEntity manageNew() {
        return managedEntityService.manageNew(TagEntity.class);
    }
}
