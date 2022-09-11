package com.github.cc007.headsplugin.integration.daos;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.PredefinedCategorizable;
import com.github.cc007.headsplugin.integration.daos.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.rest.dto.freshcoal.SkinDto;
import com.github.cc007.headsplugin.integration.rest.feign.clients.FreshCoalClient;

import feign.FeignException;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Log4j2
// TODO @ConditionalOnProperty(name = "headsplugin.provider.freshcoal", havingValue = "true", matchIfMissing = true)
public class FreshCoalDao implements PredefinedCategorizable, Searchable {

    private final FreshCoalClient client;
    private final Transformer<SkinDto, Head> headMapper;
    private final HeadspluginProperties headspluginProperties;

    @Override
    public String getDatabaseName() {
        return "FreshCoal";
    }

    @Override
    public List<Head> getCategoryHeads(@NonNull String categoryName) {
        try {
            return client.getCategory(categoryName)
                    .stream()
                    .map(headMapper::transform)
                    .collect(Collectors.toList());
        } catch (FeignException.FeignServerException ex) {
            if (!headspluginProperties.isSuppressHttpClientErrors()) {
                log.error(ex.getMessage(), ex);
            }
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getCategoryNames() {
        return List.of(
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
        try {
            return client.find(searchTerm)
                    .stream()
                    .map(headMapper::transform)
                    .collect(Collectors.toList());
        } catch (FeignException.FeignServerException ex) {
            if (!headspluginProperties.isSuppressHttpClientErrors()) {
                log.error(ex.getMessage(), ex);
            }
            return Collections.emptyList();
        }
    }
}
