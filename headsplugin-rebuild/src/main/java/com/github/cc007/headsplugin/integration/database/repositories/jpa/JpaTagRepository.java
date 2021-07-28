package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.TagEntity;
import com.github.cc007.headsplugin.integration.database.repositories.DatabaseRepository;
import com.github.cc007.headsplugin.integration.database.repositories.TagRepository;

import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Optional;

@SuperBuilder
public class JpaTagRepository extends AbstractRepository<TagEntity, Long> implements TagRepository {

    @Override
    public List<TagEntity> findAllByName(String name) {
        return findAllBy("name", name);
    }

}
