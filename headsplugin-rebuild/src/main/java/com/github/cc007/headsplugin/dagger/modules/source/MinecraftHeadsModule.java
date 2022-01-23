package com.github.cc007.headsplugin.dagger.modules.source;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.MinecraftHeadsDao;
import com.github.cc007.headsplugin.integration.rest.dto.minecraftheads.SkinDto;
import com.github.cc007.headsplugin.integration.rest.feign.clients.MinecraftHeadsClient;
import com.github.cc007.headsplugin.integration.rest.mappers.MinecraftHeadsSkinDtoToHeadMapper;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.collections4.Transformer;

import javax.inject.Singleton;

@Module
public class MinecraftHeadsModule {

    @Provides
    @Singleton
    static MinecraftHeadsDao provideMinecraftHeadsDao(
            MinecraftHeadsClient client,
            Transformer<SkinDto, Head> headMapper,
            HeadspluginProperties headspluginProperties
    ) {
        return new MinecraftHeadsDao(client, headMapper, headspluginProperties);
    }

    @Provides
    @Singleton
    static Transformer<SkinDto, Head> provideMinecraftHeadsSkinDtoToHeadMapper() {
        return new MinecraftHeadsSkinDtoToHeadMapper();
    }
}
