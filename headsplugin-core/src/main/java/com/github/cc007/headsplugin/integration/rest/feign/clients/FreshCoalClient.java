package com.github.cc007.headsplugin.integration.rest.feign.clients;

import com.github.cc007.headsplugin.integration.rest.dto.freshcoal.SkinDto;

import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface FreshCoalClient {
    @RequestLine("GET /api.php?query={searchTerm}")
    List<SkinDto> find(@Param("searchTerm") String searchTerm);

    @RequestLine("GET /mainapi.php?query={categoryName}")
    List<SkinDto> getCategory(@Param("categoryName") String categoryName);
}
