package com.github.cc007.headsplugin.integration.daos.heads.interfaces;

import com.github.cc007.headsplugin.business.domain.Head;
import java.util.List;
import java.util.Optional;
import lombok.val;

public interface Searchable
{
	default Optional<Head> getFirstHead(String searchTerm)
	{
		val heads = getHeads(searchTerm);
		if (heads.isEmpty()) {
			return Optional.empty();
		}
		else {
			return Optional.of(heads.get(0));
		}
	}

	List<Head> getHeads(String searchTerm);
}
