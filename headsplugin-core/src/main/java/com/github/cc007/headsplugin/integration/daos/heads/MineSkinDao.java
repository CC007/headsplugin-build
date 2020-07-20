package com.github.cc007.headsplugin.integration.daos.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.daos.heads.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.rest.clients.MineSkinClient;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateErrorDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateSkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinDto;
import com.github.cc007.headsplugin.integration.rest.mappers.MineSkinSkinDetailsDtoToHeadMapper;

import feign.FeignException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Log4j2
public class MineSkinDao implements Searchable, Creatable {

    private final MineSkinClient client;
    private final MineSkinSkinDetailsDtoToHeadMapper headMapper;

    @Value("${headsplugin.suppressHttpClientErrors:#{true}}")
    private boolean suppressHttpClientErrors = true;

    @Override
    public String getDatabaseName() {
        return "MineSkin";
    }

    @Override
    public Optional<Head> addHead(UUID playerUuid, String newHeadName) {
        val createDto = client.create(playerUuid.toString(), newHeadName);
        if (createDto instanceof CreateSkinDetailsDto) {
            val createSkinDetailsDto = (CreateSkinDetailsDto) createDto;
            return Optional.of(headMapper.transform(createSkinDetailsDto));
        }
        if (createDto instanceof CreateErrorDto) {
            val createErrorDto = (CreateErrorDto) createDto;
            log.error("Unable to add the head to MineSkin. The server responded with te following error: " + createErrorDto.getError());
            return Optional.empty();
        }
        log.error("An unknown type was found when trying to parse the result from adding a head to MineSkin.");
        return Optional.empty();
    }

    @Override
    public List<Head> getHeads(String searchTerm) {
        try {
            return client.find(searchTerm)
                    .getSkins()
                    .stream()
                    .map(SkinDto::getId)
                    .map(client::findById)
                    .map(headMapper::transform)
                    .collect(Collectors.toList());
        } catch (FeignException.FeignServerException ex) {
            if (!suppressHttpClientErrors) {
                log.error(ex.getMessage(), ex);
            }
            return Collections.emptyList();
        }
    }
}
