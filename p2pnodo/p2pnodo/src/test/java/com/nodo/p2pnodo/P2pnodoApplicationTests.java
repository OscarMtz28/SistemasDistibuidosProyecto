package com.nodo.p2pnodo;



import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class P2pnodoApplicationTests {

    @Test
    void contextLoads() {
        // Test vacío solo para verificar carga del contexto
    }

    @Configuration
    static class TestConfig {
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            LettuceConnectionFactory factory = new LettuceConnectionFactory();
            factory.setHostName("localhost");
            factory.setPort(6379);
            factory.setDatabase(0);
            factory.afterPropertiesSet();
            return factory;
        }
    }
}

