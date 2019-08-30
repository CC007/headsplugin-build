package com.github.cc007.headsplugin.integration.rest.clients;

import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "MinecraftHeads", url = "https://minecraft-heads.com/")
public interface MinecraftHeadsClient
{

	@RequestMapping(method = RequestMethod.GET, value = "/scripts/api.php?tags=true&cat={searchTerm}")
	SkinListDto find(@PathVariable("searchTerm") String searchTerm);
}
