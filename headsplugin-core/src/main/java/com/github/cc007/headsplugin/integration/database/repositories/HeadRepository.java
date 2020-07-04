package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HeadRepository extends CrudRepository<HeadEntity, Long> {

    List<HeadEntity> findByNameContaining(String name);

    List<HeadEntity> findByNameIgnoreCaseContaining(String name);

    Optional<HeadEntity> findByHeadOwner(String headOwner);

    List<HeadEntity> findByHeadOwnerIn(Collection<String> headOwners);

    List<HeadEntity> findByCategories_NameAndHeadOwnerIn(String categoryName, Collection<String> headOwners);

    List<HeadEntity> findByDatabases_NameAndHeadOwnerIn(String databaseName, Collection<String> headOwners);


}
