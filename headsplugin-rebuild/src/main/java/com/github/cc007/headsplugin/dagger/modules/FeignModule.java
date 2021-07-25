package com.github.cc007.headsplugin.dagger.modules;

import dagger.Module;
import dagger.Provides;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

@Module
public abstract class FeignModule {
    
    @Provides
    static GsonDecoder provideGsonDecoder() {
        return new GsonDecoder();
    }
    
    @Provides
    static GsonEncoder provideGsonEncoder() {
        return new GsonEncoder();
    }

//    @Provides
//    static FreshCoalClient provideFreshCoalClient(
//            HtmlAwareDecoder htmlAwareDecoder,
//            GsonEncoder gsonEncoder
//    ) {
//        return Feign.builder()
//                .decoder(htmlAwareDecoder)
//                .encoder(gsonEncoder)
//                .target(FreshCoalClient.class, "https://freshcoal.com/");
//    }
//
//    @Provides
//    static MinecraftHeadsClient provideMinecraftHeadsClient(
//            GsonDecoder gsonDecoder,
//            GsonEncoder gsonEncoder
//    ) {
//        return Feign.builder()
//                .decoder(gsonDecoder)
//                .encoder(gsonEncoder)
//                .target(MinecraftHeadsClient.class, "https://minecraft-heads.com/");
//    }
//
//    @Provides
//    static MineSkinClient provideMineSkinClient(
//            GsonDecoder gsonDecoder,
//            GsonEncoder gsonEncoder
//    ) {
//        return Feign.builder()
//                .decoder(gsonDecoder)
//                .encoder(gsonEncoder)
//                .target(MineSkinClient.class, "https://api.mineskin.org/");
//    }
}
