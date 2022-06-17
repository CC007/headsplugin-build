package com.github.cc007.headsplugin.integration.rest.dto.mineskin.search;

import lombok.Data;

import java.util.List;

@Data
public class SkinListDto {
    private List<SkinDto> skins;
    private PageDto page;
    private String filter;

}
