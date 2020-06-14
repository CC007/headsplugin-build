package com.github.cc007.headsplugin.integration.rest.mappers;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.SkinDetailsDto;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class MineSkinSkinDetailsDtoToHeadMapper implements Transformer<SkinDetailsDto, Head> {
    @Override
    public Head transform(@NonNull SkinDetailsDto skinDetailsDto) {
        Validate.notBlank(skinDetailsDto.getName());
        Validate.notNull(skinDetailsDto.getData());
        Validate.notBlank(skinDetailsDto.getData().getUuid());
        Validate.notNull(skinDetailsDto.getData().getTexture());
        Validate.notBlank(skinDetailsDto.getData().getTexture().getValue());

        return Head.builder()
                .name(skinDetailsDto.getName())
                .headDatabase("MineSkin")
                .headOwner(UUID.fromString(skinDetailsDto.getData().getUuid()))
                .value(fixValue(skinDetailsDto.getData().getTexture().getValue()))
                .build();
    }

    /**
     * Fix the value property by removing the containing properties before the texture property
     *
     * @param rawValue the base64 encoded value that should be modified
     * @return the fixed value
     */
    private String fixValue(String rawValue) {
        String decodedValue = new String(Base64.getDecoder().decode(rawValue), StandardCharsets.UTF_8);
        String strippedDecodedValue = "{\"textures" + decodedValue.split("textures", 2)[1];
        return Base64.getEncoder().encodeToString(strippedDecodedValue.getBytes(StandardCharsets.UTF_8));
    }
}
