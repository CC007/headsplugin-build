package com.github.cc007.headsplugin.integration.mappers;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.dto.freshcoal.SkinDto;
import java.util.UUID;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class FreshCoalHeadMapper implements Transformer<SkinDto, Head>
{
	@Override
	public Head transform(@NonNull SkinDto skinDto)
	{
		Validate.notNull(skinDto.getName());
		Validate.notBlank(skinDto.getSkullowner());
		Validate.notBlank(skinDto.getValue());

		Head head = new Head();
		head.setName(skinDto.getName());
		head.setHeadDatabase("FreshCoal");
		head.setHeadOwner(UUID.fromString(skinDto.getSkullowner()));
		head.setValue(skinDto.getValue());
		return head;
	}
}
