package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.mappers.MineSkinHeadMapper;
import com.github.cc007.headsplugin.integration.rest.clients.MineSkinClient;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MineSkinDao implements Searchable, Creatable
{

	private final MineSkinClient client;

	private final MineSkinHeadMapper headMapper;

	public MineSkinDao(MineSkinClient client, MineSkinHeadMapper headMapper)
	{
		this.client = client;
		this.headMapper = headMapper;
	}

	@Override
	public Optional<Head> addHead(Head newHead)
	{
		return Optional.empty();
	}

	@Override
	public List<Head> getHeads(String searchTerm)
	{
		return client.find(searchTerm)
			.getSkins()
			.stream()
			.map(SkinDto::getId)
			.map(client::findById)
			.map(headMapper::transform)
			.collect(Collectors.toList());
	}
}
