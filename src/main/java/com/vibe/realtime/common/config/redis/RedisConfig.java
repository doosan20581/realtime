package com.vibe.realtime.common.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * RedisTemplate 설정
     * RedisTemplate은 Redis 데이터를 조작하기 위한 핵심 클래스입니다.
     * 직렬화 방식을 지정하여, 객체를 JSON으로 안전하게 저장하고 불러올 수 있게 합니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 1. Key를 저장할 때: String 타입으로 직렬화 (기본값)
        template.setKeySerializer(new StringRedisSerializer());

        // 2. Value를 저장할 때: JSON 타입으로 직렬화 (GenericJackson2JsonRedisSerializer 사용)
        // 이 설정을 통해 Map, DTO 등을 저장하면 Redis에 { "key": "value" } 형태의 JSON 문자열로 저장됩니다.
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 3. HashKey, HashValue 설정 (필요 시 추가)
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}