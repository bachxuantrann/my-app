package bachtx.myapp.sso_service.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // Tạo ObjectMapper tùy chỉnh
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. Thêm hỗ trợ cho Java Time Module
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 2. Cấu hình Type Information (Sửa lỗi cho Jackson 2.10+)
        // Kích hoạt lưu trữ thông tin kiểu lớp để Jackson deserialize về đúng class (RefreshTokenData).
        // Sử dụng LaissezFaireSubTypeValidator (hoặc BasicPolymorphicTypeValidator.builder().allow*() để bảo mật hơn)

        objectMapper.activateDefaultTyping(
                // Validator cho phép Jackson lưu thông tin kiểu (thay thế cho null/deprecated validator)
                LaissezFaireSubTypeValidator.instance,
                DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // Tạo serializer với ObjectMapper đã cấu hình
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Cấu hình Serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}
