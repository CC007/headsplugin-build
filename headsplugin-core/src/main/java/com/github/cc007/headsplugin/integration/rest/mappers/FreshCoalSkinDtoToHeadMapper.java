package com.github.cc007.headsplugin.integration.rest.mappers;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.dto.freshcoal.SkinDto;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FreshCoalSkinDtoToHeadMapper implements Transformer<SkinDto, Head> {
    @Override
    public Head transform(@NonNull SkinDto skinDto) {
        Validate.notNull(skinDto.getName());
        Validate.notBlank(skinDto.getSkullowner());
        Validate.notBlank(skinDto.getValue());

        return Head.builder()
                .name(skinDto.getName())
                .headDatabase("FreshCoal")
                .headOwner(UUID.fromString(skinDto.getSkullowner()))
                .value(skinDto.getValue())
                .build();
    }
}
