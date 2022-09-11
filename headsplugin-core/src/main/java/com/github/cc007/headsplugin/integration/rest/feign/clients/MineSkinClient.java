package com.github.cc007.headsplugin.integration.rest.feign.clients;

import com.github.cc007.headsplugin.integration.rest.dto.mineskin.SkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateErrorDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateSkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinListDto;
import com.github.cc007.headsplugin.integration.rest.feign.decoders.statuscode.StatusCodeHandler;

import feign.Param;
import feign.RequestLine;

public interface MineSkinClient {
    @RequestLine("GET /get/list?filter={searchTerm}")
    SkinListDto find(@Param("searchTerm") String searchTerm);

    @RequestLine("GET /get/id/{id}")
    SkinDetailsDto findById(@Param("id") long id);

    @RequestLine("GET /generate/user/{uuid}?name={skinName}")
    @StatusCodeHandler(statusCode = 200, returnType = CreateSkinDetailsDto.class)
    @StatusCodeHandler(statusCode = 304, returnType = CreateSkinDetailsDto.class)
    @StatusCodeHandler(statusCode = 400, returnType = CreateErrorDto.class)
    CreateDto create(@Param("uuid") String uuid, @Param("skinName") String skinName);

}
