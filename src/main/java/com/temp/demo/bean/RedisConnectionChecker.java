package com.temp.demo.bean;

import com.temp.demo.service.RedisManagementService;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
public class RedisConnectionChecker {

    private boolean isConnectionClear;

    @Autowired
    private RedisManagementService redisManagementService;

    private static final Logger logger = LogManager.getLogger(RedisConnectionChecker.class);

    @PostConstruct
    public void init() {
        // todo create connection from swagger
        this.isConnectionClear = redisManagementService.checkConnection();
        logger.info(String.format("Connection to redis is %s", this.isConnectionClear ? "available" : "not available"));
    }
}
