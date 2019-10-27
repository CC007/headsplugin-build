package com.github.cc007.headsplugin.integration.rest.clients;

import com.github.cc007.headsplugin.integration.rest.dto.minecraftheads.SkinDto;
import com.github.cc007.headsplugin.integration.rest.dto.minecraftheads.TaggedSkinDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "MinecraftHeads", url = "https://minecraft-heads.com/")
public interface MinecraftHeadsClient {
    default List<SkinDto> find(String searchTerm) {
        throw new UnsupportedOperationException("API doesn't support this yet.");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/scripts/api.php?cat={searchTerm}")
    List<SkinDto> getCategory(@PathVariable("searchTerm") String searchTerm);

    @RequestMapping(method = RequestMethod.GET, value = "/scripts/api.php?tags=true&cat={searchTerm}")
    List<TaggedSkinDto> getCategoryWithTags(@PathVariable("searchTerm") String searchTerm);
}
