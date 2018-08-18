package com.xie.redis.springbootredis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ComponentScan(value = {"com.xie.redis.springbootredis.util.redis"})
//# 如果该文件和操作redis文件不在同一个模块下，这个scan是必须的
//使用方法：在需要添加缓存的方法上添加注解：@RedisCache（一般是dao层）
public class JedisConfig {

    private static final String JEDIS_POOL = "jedisPool";
    private static final String JEDIS_POOL_CONFIG = "jedisPoolConfig";

    @Bean(name= JEDIS_POOL)
    @Autowired
    public JedisPool jedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig config,
                                   @Value("${spring.jedis.pool.host}")String host,
                                   @Value("${spring.jedis.pool.port}")int port,
                                   @Value("${spring.jedis.pool.timeout}")int timeout,
                                   @Value("${spring.jedis.pool.password}")String password) {
            return new JedisPool(config, host, port,timeout,password);
    }

    @Bean(name= JEDIS_POOL_CONFIG)
    public JedisPoolConfig jedisPoolConfig (@Value("${spring.jedis.pool.config.maxTotal}")int maxTotal,
                                                @Value("${spring.jedis.pool.config.maxIdle}")int maxIdle,
                                                @Value("${spring.jedis.pool.config.maxWaitMillis}")int maxWaitMillis) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(maxTotal);
            config.setMaxIdle(maxIdle);
            config.setMaxWaitMillis(maxWaitMillis);
            return config;
        }


}
