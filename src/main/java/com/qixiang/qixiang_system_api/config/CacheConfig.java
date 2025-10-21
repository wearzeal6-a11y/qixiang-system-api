package com.qixiang.qixiang_system_api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * 配置Caffeine缓存管理器和不同类型数据的缓存策略
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 缓存管理器Bean
     * 配置不同类型数据的缓存过期时间和大小限制
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大缓存数量
                .maximumSize(1000)
                // 写入后过期时间（默认10分钟）
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 记录缓存命中率等统计信息
                .recordStats());
        return cacheManager;
    }

    /**
     * 组别数据缓存配置
     * 组别数据相对静态，缓存时间可以较长
     */
    @Bean("groupsCache")
    public Caffeine<Object, Object> groupsCache() {
        return Caffeine.newBuilder()
                .initialCapacity(20)
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.HOURS) // 组别数据缓存1小时
                .recordStats();
    }

    /**
     * 参赛单位数据缓存配置
     * 参赛单位数据变化不频繁，缓存时间适中
     */
    @Bean("teamsCache")
    public Caffeine<Object, Object> teamsCache() {
        return Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(200)
                .expireAfterWrite(30, TimeUnit.MINUTES) // 参赛单位数据缓存30分钟
                .recordStats();
    }

    /**
     * 赛事数据缓存配置
     * 赛事数据在比赛期间相对稳定，缓存时间适中
     */
    @Bean("eventsCache")
    public Caffeine<Object, Object> eventsCache() {
        return Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(200)
                .expireAfterWrite(30, TimeUnit.MINUTES) // 赛事数据缓存30分钟
                .recordStats();
    }

    /**
     * 运动员数据缓存配置
     * 运动员数据可能会有更新，缓存时间较短
     */
    @Bean("athletesCache")
    public Caffeine<Object, Object> athletesCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(15, TimeUnit.MINUTES) // 运动员数据缓存15分钟
                .recordStats();
    }

    /**
     * 报名数据缓存配置
     * 报名数据更新较频繁，缓存时间较短
     */
    @Bean("registrationsCache")
    public Caffeine<Object, Object> registrationsCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES) // 报名数据缓存10分钟
                .recordStats();
    }
}
