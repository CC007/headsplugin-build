package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;
import java.util.List;

public class MinecraftHeadsDao implements Searchable, Categorizable
{
	@Override
	public List<Head> getCategoryHeads(String categoryName)
	{
		return null;
	}

	@Override
	public List<String> getPredefinedCategoryNames()
	{
		return null;
	}

	@Override
	public List<String> getCustomCategoryNames()
	{
		return null;
	}

	@Override
	public List<Head> getHeads(String searchTerm)
	{
		return null;
	}
}
