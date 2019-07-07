package com.github.cc007.headsplugin.integration.rest.dto.mineskin.search;

import java.util.List;
import lombok.Data;

@Data
public class SkinListDto
{
	private List<SkinDto> skins;
	private PageDto page;
	private String filter;

}
