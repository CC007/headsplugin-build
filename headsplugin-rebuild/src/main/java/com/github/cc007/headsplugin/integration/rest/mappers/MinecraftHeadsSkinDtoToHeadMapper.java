package com.github.cc007.headsplugin.integration.rest.mappers;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.dto.minecraftheads.SkinDto;

import lombok.NonNull;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;

import java.util.UUID;

public class MinecraftHeadsSkinDtoToHeadMapper implements Transformer<SkinDto, Head> {

    @Override
    public Head transform(@NonNull SkinDto skinDto) {
        Validate.notNull(skinDto.getName());
        Validate.notBlank(skinDto.getUuid());
        Validate.notBlank(skinDto.getValue());

        return Head.builder()
                .name(skinDto.getName())
                .headDatabase("MinecraftHeads")
                .headOwner(UUID.fromString(skinDto.getUuid().trim()))
                .value(skinDto.getValue())
                .build();
    }
}
