package com.github.cc007.headsplugin.integration.rest.daos.heads;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.clients.MineSkinClient;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateErrorDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.create.CreateSkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinDto;
import com.github.cc007.headsplugin.integration.rest.mappers.MineSkinSkinDetailsDtoToHeadMapper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ToString
@Slf4j
public class MineSkinDao implements Searchable, Creatable {

    private final MineSkinClient client;

    private final MineSkinSkinDetailsDtoToHeadMapper headMapper;

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
        return client.find(searchTerm)
                .getSkins()
                .stream()
                .map(SkinDto::getId)
                .map(client::findById)
                .map(headMapper::transform)
                .collect(Collectors.toList());
    }
}
