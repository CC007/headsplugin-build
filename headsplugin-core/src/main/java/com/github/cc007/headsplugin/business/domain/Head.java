package com.github.cc007.headsplugin.business.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Head {
    private String name;
    private String value;
    private UUID headOwner;
    private String headDatabase;
}
