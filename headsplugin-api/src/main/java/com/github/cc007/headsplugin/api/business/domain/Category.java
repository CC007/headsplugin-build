package com.github.cc007.headsplugin.api.business.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Category {
    private String name;
    private List<String> databaseNames;
}
