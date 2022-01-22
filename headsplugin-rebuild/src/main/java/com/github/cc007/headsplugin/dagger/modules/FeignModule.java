package com.github.cc007.headsplugin.dagger.modules;

import com.github.cc007.headsplugin.integration.rest.feign.clients.FreshCoalClient;
import com.github.cc007.headsplugin.integration.rest.feign.clients.MineSkinClient;
import com.github.cc007.headsplugin.integration.rest.feign.clients.MinecraftHeadsClient;
import com.github.cc007.headsplugin.integration.rest.feign.decoders.HtmlAwareDecoder;
import com.github.cc007.headsplugin.integration.rest.feign.decoders.statuscode.StatusCodeHandlerDecoder;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import feign.Capability;
import feign.Feign;
import feign.codec.Decoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.val;

import javax.inject.Singleton;
import java.util.Set;

@Module
public abstract class FeignModule {

    @Provides
    @Singleton
    static GsonDecoder provideGsonDecoder() {
        return new GsonDecoder();
    }

    @Provides
    @Singleton
    static GsonEncoder provideGsonEncoder() {
        return new GsonEncoder();
    }

    @Provides
    @Singleton
    @IntoSet
    static Capability provideStatusCodeHandlerDecoderCapability() {
        return new Capability() {
            @Override
            public Decoder enrich(Decoder decoder) {
                return new StatusCodeHandlerDecoder(decoder);
            }
        };
    }

    @Provides
    @Singleton
    @IntoSet
    static Capability provideHtmlAwareDecoderCapability() {
        return new Capability() {
            @Override
            public Decoder enrich(Decoder decoder) {
                return new HtmlAwareDecoder(decoder);
            }
        };
    }

    @Provides
    @Singleton
    static FreshCoalClient provideFreshCoalClient(
            GsonDecoder gsonDecoder,
            GsonEncoder gsonEncoder,
            Set<Capability> capabilities
    ) {
        final Feign.Builder feignBuilder = getFeignBuilder(gsonDecoder, gsonEncoder, capabilities);
        return feignBuilder.target(FreshCoalClient.class, "https://freshcoal.com/");
    }

    @Provides
    @Singleton
    static MinecraftHeadsClient provideMinecraftHeadsClient(
            GsonDecoder gsonDecoder,
            GsonEncoder gsonEncoder,
            Set<Capability> capabilities
    ) {
        final Feign.Builder feignBuilder = getFeignBuilder(gsonDecoder, gsonEncoder, capabilities);
        return feignBuilder.target(MinecraftHeadsClient.class, "https://minecraft-heads.com/");
    }

    @Provides
    @Singleton
    static MineSkinClient provideMineSkinClient(
            GsonDecoder gsonDecoder,
            GsonEncoder gsonEncoder,
            Set<Capability> capabilities
    ) {
        final Feign.Builder feignBuilder = getFeignBuilder(gsonDecoder, gsonEncoder, capabilities);
        return feignBuilder.target(MineSkinClient.class, "https://api.mineskin.org/");
    }

    private static Feign.Builder getFeignBuilder(
            GsonDecoder gsonDecoder,
            GsonEncoder gsonEncoder,
            Set<Capability> capabilities) {
        val feignBuilder = Feign.builder()
                .decoder(gsonDecoder)
                .encoder(gsonEncoder);
        for (Capability capability : capabilities) {
            feignBuilder.addCapability(capability);
        }
        return feignBuilder;
    }
}
