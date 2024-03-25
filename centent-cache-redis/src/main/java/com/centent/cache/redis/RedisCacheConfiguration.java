package com.centent.cache.redis;

import com.centent.core.configuration.JacksonMapperConfiguration.LocalDateTimestampDeserializer;
import com.centent.core.configuration.JacksonMapperConfiguration.LocalDateTimestampSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.LocalDateTime;

@Configuration
@ConditionalOnProperty(name = "centent.cache.type", havingValue = "redis", matchIfMissing = true)
public class RedisCacheConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 创建一个json的序列化对象
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        // 设置value的序列化方式json
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置key序列化方式String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置hash key序列化方式String
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash value序列化json
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 设置支持事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        SimpleModule module = new SimpleModule();
        // LocalDateTime 序列化配置
        module.addSerializer(LocalDateTime.class, new LocalDateTimestampSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimestampDeserializer());

        // 创建JSON序列化器
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 必须设置，否则无法将JSON转化为对象，会转化成Map类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
