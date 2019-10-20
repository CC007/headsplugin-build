package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Categorizable;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.mappers.FreshCoalHeadMapper;
import com.github.cc007.headsplugin.integration.rest.clients.FreshCoalClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FreshCoalDao implements Searchable, Categorizable {
    private final FreshCoalClient client;

    private final FreshCoalHeadMapper headMapper;

    public FreshCoalDao(FreshCoalClient client, FreshCoalHeadMapper headMapper) {
        this.client = client;
        this.headMapper = headMapper;
    }

    @Override
    public List<Head> getCategoryHeads(String categoryName) {
        return client.getCategory(categoryName)
                .stream()
                .map(headMapper::transform)
                .collect(Collectors.toList());
    }
// todo test getCategoryHeads

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
        return client.find(searchTerm)
                .stream()
                .map(headMapper::transform)
                .collect(Collectors.toList());
    }
}
