package com.github.cc007.headsplugin.integration.rest.clients;

import com.github.cc007.headsplugin.integration.rest.dto.freshcoal.SkinDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "FreshCoal", url = "https://freshcoal.com/")
public interface FreshCoalClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api.php?query={searchTerm}")
    List<SkinDto> find(@PathVariable("searchTerm") String searchTerm);

    @RequestMapping(method = RequestMethod.GET, value = "/mainapi.php?query={categoryName}")
    List<SkinDto> getCategory(@PathVariable("categoryName") String categoryName);
}
