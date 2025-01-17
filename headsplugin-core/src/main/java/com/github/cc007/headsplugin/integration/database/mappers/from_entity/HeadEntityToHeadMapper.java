package com.github.cc007.headsplugin.integration.database.mappers.from_entity;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.DatabaseEntity;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import org.apache.commons.collections4.Transformer;

import java.util.UUID;
import java.util.stream.Collectors;

public class HeadEntityToHeadMapper implements Transformer<HeadEntity, Head> {

    @Override
    public Head transform(HeadEntity headEntity) {
        return Head.builder()
                .name(headEntity.getName())
                .headOwner(UUID.fromString(headEntity.getHeadOwner()))
                .value(headEntity.getValue())
                .headDatabase(getDatabase(headEntity))
                .build();
    }

    private String getDatabase(HeadEntity headEntity) {
        return headEntity.getDatabases()
                .stream()
                .map(DatabaseEntity::getName)
                .collect(Collectors.joining(", "));
    }
}

