package com.github.cc007.headsplugin.integration.database.mappers.domain;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;

import lombok.val;
import org.apache.commons.collections4.Transformer;
import org.springframework.stereotype.Component;

@Component
public class HeadToHeadEntityMapper implements Transformer<Head, HeadEntity> {
    @Override
    public HeadEntity transform(Head head) {
        val headEntity = new HeadEntity();
        headEntity.setName(head.getName());
        headEntity.setHeadOwner(head.getHeadOwner().toString());
        headEntity.setValue(head.getValue());
        return headEntity;
    }
}
