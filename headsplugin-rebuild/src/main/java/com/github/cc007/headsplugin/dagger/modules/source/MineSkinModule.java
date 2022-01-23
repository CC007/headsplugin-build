package com.github.cc007.headsplugin.dagger.modules.source;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.MineSkinDao;
import com.github.cc007.headsplugin.integration.rest.dto.mineskin.SkinDetailsDto;
import com.github.cc007.headsplugin.integration.rest.feign.clients.MineSkinClient;
import com.github.cc007.headsplugin.integration.rest.mappers.MineSkinSkinDetailsDtoToHeadMapper;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.collections4.Transformer;

import javax.inject.Singleton;

@Module
public class MineSkinModule {

    @Provides
    @Singleton
    static MineSkinDao provideMineSkinDao(
            MineSkinClient client,
            Transformer<SkinDetailsDto, Head> headMapper,
            HeadspluginProperties headspluginProperties
    ) {
        return new MineSkinDao(client, headMapper, headspluginProperties);
    }

    @Provides
    @Singleton
    static Transformer<SkinDetailsDto, Head> provideMineSkinSkinDetailsDtoToHeadMapper() {
        return new MineSkinSkinDetailsDtoToHeadMapper();
    }
}
