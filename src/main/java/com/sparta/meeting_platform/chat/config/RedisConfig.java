package com.sparta.meeting_platform.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
* Redis 관련 설정들을 모아놓은 class
* */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

     /*
     *  redisConnectionFactory 를 통해 외부 redis 를 연결한다.
     *  Spring 에서 Redis 접근하는 방식은 2가지의 프레임워크가 있다(Lettuce 와 Jedis)
     *  Lettuce 별도의 설정없이 사용 할 수 있지만, Jedis 사용하려면 의존성을 추가 해야만 한다.
     *  우리는 Lettuce 사용하기로 했다. (따로 설정이 필요하지않고, 성능이 더 좋다고 하여)
     * */

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /*
    * redisTemplate
    * Spring 에서 redis commend 들을 사용 할 수 있게 도와준다 (추상화 시켜준다)
    * RedisTemplate에는 serializer를 설정하여 데이터를 넘겨준다.
   */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //일반적인 Key:value 경우 serializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        //Hash 사용할 경우 serializer
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        //redis connectionFactory 사용
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
    /*
    * String RedisTemplate 설정
    * 위에 선언한 RedisTemplate 보다 좀더 문자열에 특화된 Serialize 제공한다
    */
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }

    /*
    redisPublisher 에서 메세지가 발행(publish) 되면
    MessageListener 에서 처리합니다
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }
}
