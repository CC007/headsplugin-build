package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.clients.MinecraftHeadsClient;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.PredefinedCategorizable;
import com.github.cc007.headsplugin.integration.rest.mappers.MinecraftHeadsSkinDtoToHeadMapper;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class MinecraftHeadsDao implements PredefinedCategorizable {

    private final MinecraftHeadsClient client;

    private final MinecraftHeadsSkinDtoToHeadMapper headMapper;

    @Override
    public String getDatabaseName() {
        return "MinecraftHeads";
    }

    @Override
    public List<Head> getCategoryHeads(@NonNull Category category) {
        return client.getCategory(category.getName())
                .stream()
                .map(headMapper::transform)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPredefinedCategoryNames() {
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
}
