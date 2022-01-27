package com.github.cc007.headsplugin.dagger.modules.source;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.config.properties.HeadspluginProperties;
import com.github.cc007.headsplugin.integration.daos.FreshCoalDao;
import com.github.cc007.headsplugin.integration.rest.dto.freshcoal.SkinDto;
import com.github.cc007.headsplugin.integration.rest.feign.clients.FreshCoalClient;
import com.github.cc007.headsplugin.integration.rest.mappers.FreshCoalSkinDtoToHeadMapper;

import dagger.Module;
import dagger.Provides;
import org.apache.commons.collections4.Transformer;

import javax.inject.Singleton;

@Module
public abstract class FreshCoalModule {

    @Provides
    @Singleton
    static FreshCoalDao provideFreshCoalDao(
            FreshCoalClient client,
            Transformer<SkinDto, Head> headMapper,
            HeadspluginProperties headspluginProperties
    ) {
        return new FreshCoalDao(client, headMapper, headspluginProperties);
    }

    @Provides
    @Singleton
    static Transformer<SkinDto, Head> provideFreshCoalSkinDtoToHeadMapper() {
        return new FreshCoalSkinDtoToHeadMapper();
    }
}
