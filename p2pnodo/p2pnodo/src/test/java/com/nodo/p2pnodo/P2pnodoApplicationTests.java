package com.nodo.p2pnodo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@SpringBootTest(classes = TestRedisConfig.class)
class P2pnodoApplicationTests {

    @Test
    void contextLoads() {
        // Test de carga de contexto
    }
}

@TestConfiguration
class TestRedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("localhost");
        factory.setPort(6379);
        factory.afterPropertiesSet();
        return factory;
    }
}