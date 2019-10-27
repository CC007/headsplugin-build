package com.github.cc007.headsplugin.integration.mappers;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.dto.minecraftheads.SkinDto;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MinecraftHeadsHeadMapper implements Transformer<SkinDto, Head> {

    @Override
    public Head transform(@NonNull SkinDto skinDto) {
        Validate.notNull(skinDto.getName());
        Validate.notBlank(skinDto.getUuid());
        Validate.notBlank(skinDto.getValue());

        Head head = new Head();
        head.setName(skinDto.getName());
        head.setHeadDatabase("MinecraftHeads");
        head.setHeadOwner(UUID.fromString(skinDto.getUuid().trim()));
        head.setValue(skinDto.getValue());
        return head;
    }
}
