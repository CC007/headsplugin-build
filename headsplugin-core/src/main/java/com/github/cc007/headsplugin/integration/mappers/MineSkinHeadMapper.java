package com.github.cc007.headsplugin.integration.mappers;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.SkinDetailsDto;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MineSkinHeadMapper implements Transformer<SkinDetailsDto, Head>
{
	@Override
	public Head transform(@NonNull SkinDetailsDto skinDetailsDto)
	{
		Validate.notBlank(skinDetailsDto.getName());
		Validate.notNull(skinDetailsDto.getData());
		Validate.notBlank(skinDetailsDto.getData().getUuid());
		Validate.notNull(skinDetailsDto.getData().getTexture());
		Validate.notBlank(skinDetailsDto.getData().getTexture().getValue());

		Head head = new Head();
		head.setName(skinDetailsDto.getName());
		head.setHeadDatabase("MineSkin");
		head.setHeadOwner(UUID.fromString(skinDetailsDto.getData().getUuid()));
		head.setValue(fixValue(skinDetailsDto.getData().getTexture().getValue()));
		return head;
	}

	/**
	 * Fix the value property by removing the containing properties before the texture property
	 *
	 * @param rawValue
	 * @return the fixed value
	 */
	private String fixValue(String rawValue)
	{
		String decodedValue = new String(Base64.decodeBase64(rawValue), StandardCharsets.UTF_8);
		String strippedDecodedValue = "{\"textures" + decodedValue.split("textures", 2)[1];
		return Base64.encodeBase64String(strippedDecodedValue.getBytes(StandardCharsets.UTF_8));
	}
}
