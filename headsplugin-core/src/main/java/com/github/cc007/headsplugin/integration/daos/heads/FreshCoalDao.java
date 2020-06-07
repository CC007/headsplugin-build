package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.PredefinedCategorizable;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.rest.clients.FreshCoalClient;
import com.github.cc007.headsplugin.integration.rest.mappers.FreshCoalSkinDtoToHeadMapper;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
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
@EqualsAndHashCode
public class FreshCoalDao implements PredefinedCategorizable, Searchable {

    private final FreshCoalClient client;

    private final FreshCoalSkinDtoToHeadMapper headMapper;

    @Override
    public String getDatabaseName() {
        return "FreshCoal";
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
                "food",
                "devices",
                "misc",
                "alphabet",
                "interior",
                "color",
                "blocks",
                "games",
                "mobs",
                "characters",
                "pokemon"
        );
    }

    @Override
    public List<Head> getHeads(String searchTerm) {
        if (searchTerm.length() < 3) {
            return new ArrayList<>();
        }
        return client.find(searchTerm)
                .stream()
                .map(headMapper::transform)
                .collect(Collectors.toList());
    }
}
