package com.github.cc007.headsplugin.integration.database.mappers.to_entity;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeadToDetachedHeadEntityMapper implements Transformer<Head, HeadEntity> {

    private final HeadRepository headRepository;

    @Override
    public HeadEntity transform(Head head) {
        val headEntity = new HeadEntity();
        headEntity.setHeadOwner(head.getHeadOwner().toString());
        headEntity.setName(head.getName());
        headEntity.setValue(head.getValue());

        return headEntity;
    }
}
