package com.github.cc007.headsplugin.integration.rest.dto.mineskin;

import lombok.Data;

@Data
public class TextureDto
{
	private String value;
	private String signature;
	private String url;
	private UrlsDto urls;
}
