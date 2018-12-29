package com.ly.bootadmin.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author linyun
 * @date 2018/11/23 09:27
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    /**
     * Redis服务器地址
     */
    @Value("${spring.redis.host}")
    private String host;
    /**
     * Redis服务器连接端口
     */
    @Value("${spring.redis.port}")
    private Integer port;
    /**
     * Redis数据库索引（默认为0）
     */
    @Value("${spring.redis.database}")
    private Integer database;
    /**
     * Redis服务器连接密码（默认为空）
     */
    @Value("${spring.redis.password}")
    private String password;
    /**
     * 连接超时时间（毫秒）
     */
    @Value("${spring.redis.timeout}")
    private String timeout;

    /**
     * 连接池最大连接数（使用负值表示没有限制）
     */
    @Value("${spring.redis.lettuce.pool.max-active}")
    private Integer maxActive;
    /**
     * 连接池最大阻塞等待时间（使用负值表示没有限制）
     */
    @Value("${spring.redis.lettuce.pool.max-wait}")
    private String maxWait;
    /**
     * 连接池中的最大空闲连接
     */
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private Integer maxIdle;
    /**
     * 连接池中的最小空闲连接
     */
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private Integer minIdle;
    /**
     * 关闭超时时间
     */
    @Value("${spring.redis.lettuce.shutdown-timeout}")
    private String shutdown;



}
