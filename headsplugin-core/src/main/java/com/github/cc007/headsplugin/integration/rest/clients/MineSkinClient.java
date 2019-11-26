package com.github.cc007.headsplugin.integration.rest.clients;

import com.github.cc007.headsplugin.config.aspects.statuscodes.StatusCodeHandler;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.SkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateErrorDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinListDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "MineSkin", url = "https://api.mineskin.org/")
public interface MineSkinClient {
    @RequestMapping(method = RequestMethod.GET, value = "/get/list?filter={searchTerm}")
    SkinListDto find(@PathVariable("searchTerm") String searchTerm);

    @RequestMapping(method = RequestMethod.GET, value = "/get/id/{id}")
    SkinDetailsDto findById(@PathVariable("id") long id);

    @RequestMapping(method = RequestMethod.GET, value = "/generate/user/{uuid}?name={skinName}")
    @StatusCodeHandler(statusCode = 304, returnType = SkinDetailsDto.class)
    @StatusCodeHandler(statusCode = 400, returnType = CreateErrorDto.class)
    CreateDto create(@PathVariable("uuid") String uuid, @PathVariable("skinName") String skinName);

}
