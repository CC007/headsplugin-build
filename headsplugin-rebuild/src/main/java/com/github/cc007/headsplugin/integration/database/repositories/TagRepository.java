package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.TagEntity;

import java.util.List;

public interface TagRepository extends Repository<TagEntity, Long> {

    List<TagEntity> findByName(String name);
}
