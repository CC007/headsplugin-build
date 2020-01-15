package com.github.cc007.headsplugin.integration.rest.daos.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.clients.FreshCoalClient;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.rest.mappers.FreshCoalSkinDtoToHeadMapper;

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
public class FreshCoalDao implements Searchable, Categorizable {

    private final FreshCoalClient client;

    private final FreshCoalSkinDtoToHeadMapper headMapper;

    @Override
    public String getDatabaseName() {
        return "FreshCoal";
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
    public List<String> getCustomCategoryNames() {
        //TODO base it on the config
        return new ArrayList<>();
    }

    @Override
    public List<Head> getHeads(String searchTerm) {
        if(searchTerm.length() < 3) {
            return new ArrayList<>();
        }
        return client.find(searchTerm)
                .stream()
                .map(headMapper::transform)
                .collect(Collectors.toList());
    }
}
