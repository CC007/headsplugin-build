package com.github.cc007.headsplugin.integration.rest.dto.minecraftheads;

import lombok.Data;

@Data
public class TaggedSkinDto {
    private String name;
    private String uuid;
    private String value;
    private String tags;
}