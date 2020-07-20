package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.api.business.domain.Category;
import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.PredefinedCategorizable;
import com.github.cc007.headsplugin.integration.rest.clients.MinecraftHeadsClient;
import com.github.cc007.headsplugin.integration.rest.mappers.MinecraftHeadsSkinDtoToHeadMapper;

import feign.FeignException;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Log4j2
public class MinecraftHeadsDao implements PredefinedCategorizable {

    private final MinecraftHeadsClient client;
    private final MinecraftHeadsSkinDtoToHeadMapper headMapper;

    @Value("${headsplugin.suppressHttpClientErrors:#{true}}")
    private boolean suppressHttpClientErrors = true;

    @Override
    public String getDatabaseName() {
        return "MinecraftHeads";
    }

    @Override
    public List<Head> getCategoryHeads(@NonNull Category category) {
        try {
            return client.getCategory(category.getName())
                    .stream()
                    .map(headMapper::transform)
                    .collect(Collectors.toList());
        } catch (FeignException.FeignServerException ex) {
            if (!suppressHttpClientErrors) {
                log.error(ex.getMessage(), ex);
            }
            return Collections.emptyList();
        }
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
