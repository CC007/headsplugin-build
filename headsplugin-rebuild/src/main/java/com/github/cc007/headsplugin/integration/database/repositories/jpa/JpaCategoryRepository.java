package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;

import lombok.experimental.SuperBuilder;

import java.util.Optional;

@SuperBuilder
public class JpaCategoryRepository extends AbstractRepository<CategoryEntity, Long> implements CategoryRepository {

    @Override
    public Optional<CategoryEntity> findByName(String name) {
        return findBy("name", name);
    }

}
