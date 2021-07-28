package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuperBuilder
public class JpaHeadRepository extends AbstractRepository<HeadEntity, Long> implements HeadRepository {
    @Override
    public List<HeadEntity> findAllByNameIgnoreCaseContaining(String name) {
        return null; //TODO implement
    }

    @Override
    public Optional<HeadEntity> findByHeadOwner(String headOwner) {
        return findBy("headOwner", headOwner);
    }

    @Override
    public List<HeadEntity> findAllByHeadOwnerIn(Collection<String> headOwners) {
        return null; //TODO implement
    }

    @Override
    public List<HeadEntity> findAllByDatabases_NameAndHeadOwnerIn(String databaseName, Collection<String> headOwners) {
        return null; //TODO implement
    }
}
