package com.github.cc007.headsplugin.api.business.services.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HeadSearcher {
    int getSearchCount(String searchTerm);

    Optional<Head> getHeads(UUID headOwner);

    List<Head> getHeads(String searchTerm);
}
