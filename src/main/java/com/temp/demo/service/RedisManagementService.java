package com.temp.demo.service;

import com.temp.demo.bean.RedisConnectionChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisManagementService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisConnectionChecker redisConnectionChecker;

    private static final Logger logger = LogManager.getLogger(RedisManagementService.class);

    public boolean checkConnection() {
        logger.info("Checking connection to Redis server");
        try {
            stringRedisTemplate.hasKey("test_key");
            return Boolean.TRUE;
        } catch (RedisConnectionFailureException exception) {
            return Boolean.FALSE;
        }
    }

    public String getValueFromRedis(String key) {
        if(redisConnectionChecker.isConnectionClear()) {
            try {
                Boolean hasKey = stringRedisTemplate.hasKey(key);
                BoundValueOperations<String, String> vOps = stringRedisTemplate.boundValueOps(key);
                if(Boolean.TRUE.equals(hasKey)) {
                    String string = vOps.get();
                    if(StringUtils.hasLength(string))
                        return string;
                }
            } catch (RedisConnectionFailureException exception) {
                logger.warn("Redis connection failure, closing the connection temporarily");
                redisConnectionChecker.setConnectionClear(Boolean.FALSE);
            } catch (Exception exception) {
                logger.warn("Un catch failed to get from Redis");
            }
        }
        return null;
    }

    public Boolean setValueToRedis(String key, String value, int timeout, TimeUnit timeUnit) {
        if(redisConnectionChecker.isConnectionClear()) {
            try {
                BoundValueOperations<String, String> vOps = stringRedisTemplate.boundValueOps(key);
                return vOps.setIfAbsent(value, timeout, timeUnit);
            } catch (RedisConnectionFailureException exception) {
                logger.warn("Redis connection failure, closing the connection temporarily");
                redisConnectionChecker.setConnectionClear(Boolean.FALSE);
            } catch (Exception exception) {
                logger.warn("Un catch failed to save to Redis");
            }
        }
        return null;
    }

    public void setValueToRedis(String key, double score, String value) {
        if(redisConnectionChecker.isConnectionClear()) {
            try {
                Boolean hasKey = stringRedisTemplate.hasKey(key);
                BoundZSetOperations<String, String> zSetOps = stringRedisTemplate.boundZSetOps(key);
                if(Boolean.TRUE.equals(hasKey))
                    zSetOps.removeRangeByScore(score, score);
                zSetOps.addIfAbsent(value, score);
                zSetOps.expire(6, TimeUnit.HOURS);
            } catch (RedisConnectionFailureException exception) {
                logger.warn("Redis connection failure, closing the connection temporarily");
                redisConnectionChecker.setConnectionClear(Boolean.FALSE);
            } catch (Exception exception) {
                logger.warn("Un catch failed to save to Redis");
            }
        }
    }

    public String getValueFromRedis(String key, double score) {
        if(redisConnectionChecker.isConnectionClear()) {
            try {
                Boolean hasKey = stringRedisTemplate.hasKey(key);
                BoundZSetOperations<String, String> zSetOps = stringRedisTemplate.boundZSetOps(key);
                if(Boolean.TRUE.equals(hasKey)) {
                    Set<String> strings = zSetOps.rangeByScore(score, score);
                    if(!CollectionUtils.isEmpty(strings)) {
                        return strings.iterator().next();
                    }
                }
            } catch (RedisConnectionFailureException exception) {
                logger.warn("Redis connection failure, closing the connection temporarily");
                redisConnectionChecker.setConnectionClear(Boolean.FALSE);
            } catch (Exception exception) {
                logger.warn("Un catch failed to get from Redis");
            }
        }
        return null;
    }
}
