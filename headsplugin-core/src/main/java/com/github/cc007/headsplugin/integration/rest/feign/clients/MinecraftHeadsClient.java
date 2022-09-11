package com.github.cc007.headsplugin.integration.rest.feign.clients;

import com.github.cc007.headsplugin.integration.rest.dto.minecraftheads.SkinDto;
import com.github.cc007.headsplugin.integration.rest.dto.minecraftheads.TaggedSkinDto;

import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface MinecraftHeadsClient {
    default List<SkinDto> find(String searchTerm) {
        throw new UnsupportedOperationException("API doesn't support this yet.");
    }

    @RequestLine("GET /scripts/api.php?cat={searchTerm}")
    List<SkinDto> getCategory(@Param("searchTerm") String searchTerm);

    @RequestLine("GET /scripts/api.php?tags=true&cat={searchTerm}")
    List<TaggedSkinDto> getCategoryWithTags(@Param("searchTerm") String searchTerm);
}
