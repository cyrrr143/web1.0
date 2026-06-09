package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration          // 标记这是一个配置类，Spring会扫描并加载它
@Slf4j                  // Lombok注解，自动生成log日志对象
public class RedisConfiguration {

    @Bean               // 将该方法的返回值注册为Spring Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){

        log.info("开始创建redis模板对象...");  // 打印日志

        RedisTemplate redisTemplate = new RedisTemplate();  // 创建RedisTemplate实例

        // 设置redis的连接工厂对象（用于创建Redis连接）
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置redis key的序列化器（将key以字符串形式存储，避免乱码）
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        return redisTemplate;  // 返回配置好的对象
    }

    /**
     * 创建StringRedisTemplate用于操作字符串类型的数据
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建字符串redis模板对象...");
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }
}