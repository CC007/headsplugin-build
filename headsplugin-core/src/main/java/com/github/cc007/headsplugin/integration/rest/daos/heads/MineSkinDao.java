package com.github.cc007.headsplugin.integration.rest.daos.heads;

import com.github.cc007.headsplugin.business.domain.Head;
import com.github.cc007.headsplugin.integration.rest.clients.MineSkinClient;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Creatable;
import com.github.cc007.headsplugin.integration.rest.daos.heads.interfaces.Searchable;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.search.SkinDto;
import com.github.cc007.headsplugin.integration.rest.mappers.MineSkinSkinDetailsDtoToHeadMapper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ToString
public class MineSkinDao implements Searchable, Creatable {

    private final MineSkinClient client;

    private final MineSkinSkinDetailsDtoToHeadMapper headMapper;

    @Override
    public String getDatabaseName() {
        return "MineSkin";
    }

    @Override
    public Optional<Head> addHead(Head newHead) {
        return Optional.empty();
    } //TODO implement

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
