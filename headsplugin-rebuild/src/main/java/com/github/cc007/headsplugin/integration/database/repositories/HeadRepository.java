package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HeadRepository extends Repository<HeadEntity, Long> {

    List<HeadEntity> findAllByNameIgnoreCaseContaining(String name);

    Optional<HeadEntity> findByHeadOwner(String headOwner);

    List<HeadEntity> findAllByHeadOwnerIn(Collection<String> headOwners);

    List<HeadEntity> findAllByDatabases_NameAndHeadOwnerIn(String databaseName, Collection<String> headOwners);


}
