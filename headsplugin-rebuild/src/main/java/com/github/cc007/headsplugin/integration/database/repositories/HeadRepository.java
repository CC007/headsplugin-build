package com.github.cc007.headsplugin.integration.database.repositories;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HeadRepository extends Repository<HeadEntity, Long> {

    Optional<HeadEntity> findByHeadOwner(String headOwner);

    List<HeadEntity> findAllByHeadOwnerIn(Collection<String> headOwners);

    List<String> findAllHeadOwnersByHeadOwnerIn(Collection<String> headOwners);

    List<HeadEntity> findAllByDatabases_NameAndHeadOwnerIn(String databaseName, Collection<String> headOwners);

    List<HeadEntity> findAllByNameIgnoreCaseContaining(String name);

    default HeadEntity createFromHead(Head head) {
        val headEntity = manageNew();
        headEntity.setHeadOwner(head.getHeadOwner().toString());
        headEntity.setName(head.getName());
        headEntity.setValue(head.getValue());
        return headEntity;
    }
}
