package com.github.cc007.headsplugin.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
//	@Bean
//	public CacheManager cacheManager(){
//		PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//			.with(CacheManagerBuilder.persistence(headsPlugin.getDataFolder().toPath().resolve("headscache").toFile()))
//			.withCache("persistent-cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
//				ResourcePoolsBuilder.newResourcePoolsBuilder().disk(100, MemoryUnit.MB, true))
//			)
//			.build(true);
//		return persistentCacheManager;
//	}
}
