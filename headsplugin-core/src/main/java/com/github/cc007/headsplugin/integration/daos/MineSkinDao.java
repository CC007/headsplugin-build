package com.github.cc007.headsplugin.integration.daos;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.daos.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.SkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateErrorDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateSkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinDto;
import com.github.cc007.headsplugin.integration.rest.feign.clients.MineSkinClient;

import feign.FeignException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.Transformer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Log4j2
public class MineSkinDao implements Searchable, Creatable {

    private final MineSkinClient client;
    private final Transformer<SkinDetailsDto, Head> headMapper;
    private final HeadspluginProperties headspluginProperties;

    @Override
    public String getDatabaseName() {
        return "MineSkin";
    }

    @Override
    public Optional<Head> addHead(UUID playerUuid, String newHeadName) {
        if (headspluginProperties.getProvider().isMineskin()) {
            final var createDto = client.create(playerUuid.toString(), newHeadName);
            if (createDto instanceof CreateSkinDetailsDto createSkinDetailsDto) {
                return Optional.of(headMapper.transform(createSkinDetailsDto));
            }
            if (createDto instanceof CreateErrorDto createErrorDto) {
                log.error("Unable to add the head to MineSkin. The server responded with te following error: " + createErrorDto.getError());
                return Optional.empty();
            }
            log.error("An unknown type was found when trying to parse the result from adding a head to MineSkin.");
        }
        return Optional.empty();
    }

    @Override
    public List<Head> getHeads(String searchTerm) {
        if (headspluginProperties.getProvider().isMineskin()) {
            try {
                return client.find(searchTerm)
                        .getSkins()
                        .stream()
                        .map(SkinDto::getId)
                        .map(client::findById)
                        .map(headMapper::transform)
                        .collect(Collectors.toList());
            } catch (FeignException.FeignServerException | FeignException.NotFound ex) {
                if (!headspluginProperties.isSuppressHttpClientErrors()) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
        return List.of();
    }
}
