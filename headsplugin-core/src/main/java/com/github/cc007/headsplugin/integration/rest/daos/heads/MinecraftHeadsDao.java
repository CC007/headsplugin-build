package com.github.cc007.headsplugin.integration.rest.daos.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.clients.MinecraftHeadsClient;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.rest.mappers.MinecraftHeadsSkinDtoToHeadMapper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ToString
public class MinecraftHeadsDao implements Categorizable {

    private final MinecraftHeadsClient client;

    private final MinecraftHeadsSkinDtoToHeadMapper headMapper;

    @Override
    public String getDatabaseName() {
        return "MinecraftHeads";
    }

    @Override
    public List<Head> getCategoryHeads(String categoryName) {
        return client.getCategory(categoryName)
                .stream()
                .map(headMapper::transform)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPredefinedCategoryNames() {
        //TODO base it on the config
        return Arrays.asList(
                "alphabet",
                "animals",
                "blocks",
                "decoration",
                "food-drinks",
                "humans",
                "humanoid",
                "miscellaneous",
                "monsters",
                "plants"
        );
    }

    @Override
    public List<String> getCustomCategoryNames() {
        //TODO base it on the config
        return new ArrayList<>();
    }
}
