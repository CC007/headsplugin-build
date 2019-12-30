package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeadRepository extends CrudRepository<HeadEntity, Long> {

    List<HeadEntity> findByNameContaining(String name);

    Optional<HeadEntity> findByHeadOwner(String headOwner);

    List<HeadEntity> findByHeadOwnerIn(List<String> headOwners);

    List<HeadEntity> findByCategories_NameAndHeadOwnerIn(String categoryName, List<String> headOwners);

    List<HeadEntity> findByDatabases_NameAndHeadOwnerIn(String databaseName, List<String> headOwners);
}
